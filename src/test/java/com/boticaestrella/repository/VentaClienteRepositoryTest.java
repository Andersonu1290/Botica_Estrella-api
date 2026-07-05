package com.boticaestrella.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.modelo.DetalleVentaCliente;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.modelo.VentaCliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VentaClienteRepositoryTest {

    @Autowired
    private VentaClienteRepository ventaClienteRepository;

    @Autowired
    private DetalleVentaClienteRepository detalleVentaClienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void derivedAndDashboardQueries_returnExpectedValues() {
        String sufijo = String.valueOf(Math.abs((int) (System.nanoTime() % 10000)));
        Usuario usuario = usuarioRepository.saveAndFlush(crearUsuario("user-web-" + sufijo));
        int idUsuario = usuario.getIdUsuario();
        Producto producto = guardarProducto("SKU-WEB-1-" + sufijo, "Jarabe-" + sufijo);
        double ingresosAntes = ventaClienteRepository.obtenerTotalIngresosWeb();
        int completadasAntes = ventaClienteRepository.contarVentasCompletadasWeb();
        long totalVentasAntes = ventaClienteRepository.countTotalVentas();

        VentaCliente venta1 = ventaClienteRepository.saveAndFlush(crearVenta(idUsuario, "PED-2026-" + sufijo, "PAGADO", 40.0));
        VentaCliente venta2 = ventaClienteRepository.saveAndFlush(crearVenta(idUsuario, "PED-2026-" + (Integer.parseInt(sufijo) + 1), "CANCELADO", 20.0));

        detalleVentaClienteRepository.save(crearDetalle(venta1.getIdVentaCliente(), producto.getIdProducto(), 2, 20.0, 40.0));
        detalleVentaClienteRepository.save(crearDetalle(venta2.getIdVentaCliente(), producto.getIdProducto(), 1, 20.0, 20.0));

        assertEquals(2, ventaClienteRepository.findByIdUsuario(idUsuario).size());
        assertEquals("PED-2026-" + sufijo, ventaClienteRepository.findByNroPedido("PED-2026-" + sufijo).orElseThrow().getNroPedido());
        assertFalse(ventaClienteRepository.findByEstado("PAGADO").isEmpty());
        assertEquals(1, ventaClienteRepository.findByIdUsuarioAndEstado(idUsuario, "PAGADO").size());
        assertEquals(totalVentasAntes + 2, ventaClienteRepository.countTotalVentas());
        assertTrue(ventaClienteRepository.findPendingShipments().stream()
            .allMatch(venta -> "PAGADO".equals(venta.getEstado()) || "PROCESANDO".equals(venta.getEstado())));
        assertEquals(ingresosAntes + 40.0, ventaClienteRepository.obtenerTotalIngresosWeb());
        assertEquals(completadasAntes + 1, ventaClienteRepository.contarVentasCompletadasWeb());

        List<Object[]> topProductos = ventaClienteRepository.obtenerTopProductosWeb();
        assertFalse(topProductos.isEmpty());
        assertTrue(topProductos.stream().anyMatch(fila -> ("Jarabe-" + sufijo).equals(fila[0])
            && ((Number) fila[1]).longValue() == 2L
                && "Salud".equals(fila[2])));
    }

    @Test
    void findAll_and_sorting_helpers_canSupportServiceQueries() {
        String sufijo = String.valueOf(Math.abs((int) (System.nanoTime() % 10000)));
        Usuario usuario = usuarioRepository.saveAndFlush(crearUsuario("user-sort-" + sufijo));
        int idUsuario = usuario.getIdUsuario();
        ventaClienteRepository.saveAndFlush(crearVenta(idUsuario, "PED-2026-" + sufijo, "PENDIENTE", 15.0));
        ventaClienteRepository.saveAndFlush(crearVenta(idUsuario, "PED-2026-" + (Integer.parseInt(sufijo) + 1), "PENDIENTE", 25.0));

        List<VentaCliente> ventas = ventaClienteRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaPedido"));
        assertTrue(ventas.stream().anyMatch(venta -> venta.getNroPedido().equals("PED-2026-" + (Integer.parseInt(sufijo) + 1))));
    }

    private VentaCliente crearVenta(int idUsuario, String nroPedido, String estado, double total) {
        VentaCliente venta = new VentaCliente();
        venta.setIdUsuario(idUsuario);
        venta.setNroPedido(nroPedido);
        venta.setEstado(estado);
        venta.setFechaPedido(LocalDateTime.of(2026, 7, 4, 10, 30));
        venta.setSubtotal(total);
        venta.setCostoEnvio(0.0);
        venta.setTotal(total);
        venta.setDniCliente("12345678");
        venta.setNombreCliente("Ana");
        venta.setEmailCliente("ana@example.com");
        venta.setDireccionEnvio("Av. Principal 123");
        venta.setCiudad("Lima");
        venta.setDepartamento("Lima");
        venta.setTipoEnvio("NORMAL");
        venta.setTipoPago("YAPE");
        return venta;
    }

    private DetalleVentaCliente crearDetalle(int idVentaCliente, int idProducto, int cantidad, double precioUnitario, double subtotal) {
        DetalleVentaCliente detalle = new DetalleVentaCliente();
        detalle.setIdVentaCliente(idVentaCliente);
        detalle.setIdProducto(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        return detalle;
    }

    private Producto guardarProducto(String sku, String nombre) {
        Categoria categoria = categoriaRepository.save(crearCategoria("Salud"));
        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(10);
        producto.setStockMinimo(2);
        producto.setPrecio(20.0);
        return productoRepository.save(producto);
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Usuario crearUsuario(String username) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword("secret");
        usuario.setRol("CLIENTE");
        return usuario;
    }
}