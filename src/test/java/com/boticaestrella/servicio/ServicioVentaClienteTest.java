package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.boticaestrella.dto.ItemCarritoDTO;
import com.boticaestrella.dto.PedidoClienteRequestDTO;
import com.boticaestrella.dto.PedidoClienteResponseDTO;
import com.boticaestrella.modelo.DetalleVentaCliente;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;
import com.boticaestrella.modelo.VentaCliente;
import com.boticaestrella.patrones.observer.GestorStock;
import com.boticaestrella.repository.DetalleVentaClienteRepository;
import com.boticaestrella.repository.KardexRepository;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;
import com.boticaestrella.repository.VentaClienteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioVentaClienteTest {

    @Mock
    private VentaClienteRepository ventaClienteRepository;

    @Mock
    private DetalleVentaClienteRepository detalleVentaClienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private GestorStock gestorStock;

    @Mock
    private SeriesRepository seriesRepository;

    @Captor
    private ArgumentCaptor<VentaCliente> ventaCaptor;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    private ServicioVentaCliente servicioVentaCliente;

    @BeforeEach
    void setUp() {
        servicioVentaCliente = new ServicioVentaCliente(
                ventaClienteRepository,
                detalleVentaClienteRepository,
                productoRepository,
                kardexRepository,
                gestorStock,
                seriesRepository
        );
    }

    @Test
    void crearPedido_success_createsPedidoAndUpdatesStock() throws Exception {
        PedidoClienteRequestDTO request = crearRequest(7, 2, 15.0, 5.0, "NORMAL");
        Producto producto = crearProducto(1, 3, 3, "SKU-1", "Gel Antibacterial");
        List<Series> seriesDisponibles = crearSeriesDisponibles(1, 2);

        when(ventaClienteRepository.countTotalVentas()).thenReturn(0L);
        when(ventaClienteRepository.save(any(VentaCliente.class))).thenAnswer(invocation -> {
            VentaCliente venta = invocation.getArgument(0);
            venta.setIdVentaCliente(99);
            return venta;
        });
        when(detalleVentaClienteRepository.save(any(DetalleVentaCliente.class))).thenAnswer(invocation -> {
            DetalleVentaCliente detalle = invocation.getArgument(0);
            detalle.setIdDetalle(11);
            return detalle;
        });
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(seriesRepository.findByIdProductoAndEstado(1, "DISPONIBLE")).thenReturn(seriesDisponibles);
        when(seriesRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(kardexRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoClienteResponseDTO response = servicioVentaCliente.crearPedido(request);

        assertNotNull(response);
        assertEquals(99, response.getIdVentaCliente());
        assertEquals("PED-2026-00001", response.getNroPedido());
        assertEquals(35.0, response.getTotal());
        assertEquals(1, response.getDetalles().size());
        assertEquals("Gel Antibacterial", response.getDetalles().get(0).getNombreProducto());

        verify(productoRepository).save(productoCaptor.capture());
        assertEquals(1, productoCaptor.getValue().getStockActual());
        verify(gestorStock).dispararAlertaStockCritico("SKU-1", 1);
        verify(kardexRepository).save(any());
        verify(ventaClienteRepository).save(ventaCaptor.capture());
        assertEquals(35.0, ventaCaptor.getValue().getTotal());
    }

    @Test
    void obtenerPedidoPorId_returnsMappedResponse() throws Exception {
        VentaCliente venta = crearVenta(15, 7, "PED-2026-00015", "PENDIENTE");
        DetalleVentaCliente detalle = crearDetalle(1, 15, 1, 2, 15.0, 30.0, 0.0);
        Producto producto = crearProducto(1, 10, 3, "SKU-1", "Jarabe");

        when(ventaClienteRepository.findById(15)).thenReturn(Optional.of(venta));
        when(detalleVentaClienteRepository.findByIdVentaCliente(15)).thenReturn(List.of(detalle));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        PedidoClienteResponseDTO response = servicioVentaCliente.obtenerPedidoPorId(15);

        assertEquals(15, response.getIdVentaCliente());
        assertEquals("PED-2026-00015", response.getNroPedido());
        assertEquals(1, response.getDetalles().size());
        assertEquals("Jarabe", response.getDetalles().get(0).getNombreProducto());
    }

    @Test
    void cancelarPedido_success_restoresStockAndMarksPedidoCanceled() throws Exception {
        VentaCliente venta = crearVenta(18, 8, "PED-2026-00018", "PENDIENTE");
        DetalleVentaCliente detalle = crearDetalle(2, 18, 1, 2, 15.0, 30.0, 0.0);
        Producto producto = crearProducto(1, 3, 3, "SKU-2", "Crema");
        List<Series> seriesAsignadas = crearSeriesAsignadas(1, 2);

        when(ventaClienteRepository.findById(18)).thenReturn(Optional.of(venta));
        when(detalleVentaClienteRepository.findByIdVentaCliente(18)).thenReturn(List.of(detalle));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(seriesRepository.findByIdProductoAndEstado(1, "ASIGNADO")).thenReturn(seriesAsignadas);
        when(seriesRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(kardexRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaClienteRepository.save(any(VentaCliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        servicioVentaCliente.cancelarPedido(18);

        verify(productoRepository).save(productoCaptor.capture());
        assertEquals(5, productoCaptor.getValue().getStockActual());
        verify(seriesRepository).saveAll(any());
        verify(kardexRepository).save(any());
        verify(ventaClienteRepository).save(ventaCaptor.capture());
        assertEquals("CANCELADO", ventaCaptor.getValue().getEstado());
    }

    @Test
    void crearPedido_withoutItems_throwsException() {
        PedidoClienteRequestDTO request = new PedidoClienteRequestDTO(
                7,
                "12345678",
                "Ana",
                "Perez",
                "ana@example.com",
                "999999999",
                "Av. Principal 123",
                "123",
                "Dpto 2",
                "Lima",
                "Lima",
                "15001",
                "NORMAL",
                5.0,
                "YAPE",
                null,
                null,
                null,
                null,
                new ArrayList<>(),
                "Sin observaciones"
        );

        Exception error = assertThrows(Exception.class, () -> servicioVentaCliente.crearPedido(request));
        assertEquals("Error al crear el pedido: El pedido debe contener al menos un producto", error.getMessage());
        verify(ventaClienteRepository, never()).save(any());
    }

    private PedidoClienteRequestDTO crearRequest(int idUsuario, int cantidad, double precioUnitario, double costoEnvio, String tipoEnvio) {
        return new PedidoClienteRequestDTO(
                idUsuario,
                "12345678",
                "Ana",
                "Perez",
                "ana@example.com",
                "999999999",
                "Av. Principal 123",
                "123",
                "Dpto 2",
                "Lima",
                "Lima",
                "15001",
                tipoEnvio,
                costoEnvio,
                "YAPE",
                null,
                null,
                null,
                null,
                List.of(new ItemCarritoDTO(1, cantidad, precioUnitario)),
                "Entregar en horario de oficina"
        );
    }

    private VentaCliente crearVenta(int idVentaCliente, int idUsuario, String nroPedido, String estado) {
        VentaCliente venta = new VentaCliente();
        venta.setIdVentaCliente(idVentaCliente);
        venta.setIdUsuario(idUsuario);
        venta.setNroPedido(nroPedido);
        venta.setEstado(estado);
        venta.setFechaPedido(LocalDateTime.of(2026, 7, 4, 10, 30));
        venta.setFechaEntregaEstimada(LocalDate.of(2026, 7, 9));
        venta.setSubtotal(30.0);
        venta.setCostoEnvio(5.0);
        venta.setTotal(35.0);
        venta.setNombreCliente("Ana");
        venta.setEmailCliente("ana@example.com");
        venta.setTelefonoCliente("999999999");
        venta.setDireccionEnvio("Av. Principal 123");
        venta.setCiudad("Lima");
        venta.setDepartamento("Lima");
        venta.setTipoEnvio("NORMAL");
        venta.setTipoPago("YAPE");
        return venta;
    }

    private DetalleVentaCliente crearDetalle(int idDetalle, int idVentaCliente, int idProducto, int cantidad,
                                             double precioUnitario, double subtotal, double descuento) {
        DetalleVentaCliente detalle = new DetalleVentaCliente();
        detalle.setIdDetalle(idDetalle);
        detalle.setIdVentaCliente(idVentaCliente);
        detalle.setIdProducto(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        detalle.setDescuento(descuento);
        return detalle;
    }

    private Producto crearProducto(int idProducto, int stockActual, int stockMinimo, String sku, String nombre) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setStockActual(stockActual);
        producto.setStockMinimo(stockMinimo);
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        return producto;
    }

    private List<Series> crearSeriesDisponibles(int idProducto, int cantidad) {
        List<Series> series = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Series serie = new Series();
            serie.setIdProducto(idProducto);
            serie.setEstado("DISPONIBLE");
            serie.setNumeroSerie("SER-DISP-" + i);
            series.add(serie);
        }
        return series;
    }

    private List<Series> crearSeriesAsignadas(int idProducto, int cantidad) {
        List<Series> series = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Series serie = new Series();
            serie.setIdProducto(idProducto);
            serie.setEstado("ASIGNADO");
            serie.setNumeroSerie("SER-ASI-" + i);
            series.add(serie);
        }
        return series;
    }
}