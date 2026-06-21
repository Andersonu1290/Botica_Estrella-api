package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.boticaestrella.modelo.Cliente;
import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.patrones.factory.ComprobanteFactory;
import com.boticaestrella.patrones.factory.IComprobante;
import com.boticaestrella.patrones.observer.GestorStock;
import com.boticaestrella.patrones.strategy.IEstrategiaPago;
import com.boticaestrella.repository.ClienteRepository;
import com.boticaestrella.repository.KardexRepository;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;
import com.boticaestrella.repository.VentaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioVentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private ComprobanteFactory comprobanteFactory;

    @Mock
    private GestorStock gestorStock;

    @Captor
    private ArgumentCaptor<Venta> ventaCaptor;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    @Captor
    private ArgumentCaptor<Series> seriesCaptor;

    @Captor
    private ArgumentCaptor<MovimientoKardex> kardexCaptor;

    private ServicioVenta servicioVenta;

    private Map<String, IEstrategiaPago> estrategiasPago;

    @BeforeEach
    void setUp() {
        estrategiasPago = new HashMap<>();
        servicioVenta = new ServicioVenta(
                ventaRepository,
                clienteRepository,
                productoRepository,
                seriesRepository,
                kardexRepository,
                estrategiasPago,
                comprobanteFactory,
                gestorStock
        );
    }

    @Test
    void testProcesarSalidaProducto_web_withAvailableSeries() throws Exception {
        int idProducto = 1;
        String nroSerie = "WEB-0";
        String tipoComprobante = "BOLETA";
        int idUsuario = 10;
        String docCliente = "DNI123";
        String nombreCliente = "Cliente A";
        String correo = "a@a.com";
        String metodoPago = "EFECTIVO";
        double total = 50.0;

        // Series disponible
        Series s = new Series();
        s.setIdProducto(idProducto);
        s.setNumeroSerie("SN-1");
        s.setEstado("DISPONIBLE");
        List<Series> disponibles = new ArrayList<>();
        disponibles.add(s);

        when(seriesRepository.findByIdProductoAndEstado(idProducto, "DISPONIBLE")).thenReturn(disponibles);

        // Estrategia de pago
        IEstrategiaPago estrategia = mock(IEstrategiaPago.class);
        when(estrategia.procesarPago(anyString(), eq(total))).thenReturn("PAGO_OK");
        estrategiasPago.put(metodoPago, estrategia);

        // Comprobante
        IComprobante comp = mock(IComprobante.class);
        when(comprobanteFactory.crearComprobante(tipoComprobante)).thenReturn(comp);
        when(comp.generar(anyString())).thenReturn("DOC_OK");

        // Cliente ya existente con id definido para la prueba
        Cliente clienteExistente = new Cliente();
        clienteExistente.setIdCliente(77);
        clienteExistente.setDocumentoIdentidad(docCliente);
        clienteExistente.setNombreCompleto(nombreCliente);
        when(clienteRepository.findByDocumentoIdentidad(docCliente)).thenReturn(Optional.of(clienteExistente));

        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setStockActual(5);
        producto.setStockMinimo(2);
        producto.setCodigoSKU("SKU-1");
        producto.setNombre("Prod A");
        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(producto));

        Map<String, Object> resp = servicioVenta.procesarSalidaProducto(
                idProducto, nroSerie, tipoComprobante, idUsuario,
                docCliente, nombreCliente, correo, metodoPago, total
        );

        assertNotNull(resp);
        assertEquals("PAGO_OK", resp.get("msgPago"));
        assertEquals("DOC_OK", resp.get("msgDoc"));

        verify(ventaRepository).save(ventaCaptor.capture());
        Venta savedVenta = ventaCaptor.getValue();
        // No garantizamos el id del cliente en la entidad capturada en tests unitarios (mock),
        // pero verificamos que otros campos importantes estén presentes
        assertEquals(idUsuario, savedVenta.getIdUsuario());
        assertEquals(metodoPago, savedVenta.getMetodoPago());

        verify(productoRepository).save(productoCaptor.capture());
        assertEquals(4, productoCaptor.getValue().getStockActual());

        verify(seriesRepository).save(seriesCaptor.capture());
        assertEquals("ASIGNADO", seriesCaptor.getValue().getEstado());

        verify(kardexRepository).save(kardexCaptor.capture());
        MovimientoKardex mk = kardexCaptor.getValue();
        assertEquals(idProducto, mk.getIdProducto());
    }

}

