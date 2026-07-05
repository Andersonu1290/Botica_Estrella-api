package com.boticaestrella.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.modelo.Cliente;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.modelo.Venta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void queryMethods_returnExpectedDashboardValues() {
        Cliente cliente = clienteRepository.save(crearCliente("Juan Perez"));
        Usuario usuario = usuarioRepository.save(crearUsuario("user-dashboard"));
        Categoria categoria = categoriaRepository.save(crearCategoria("Analgesicos"));
        Producto producto = productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-V1", "Paracetamol"));

        double ingresosAntes = ventaRepository.obtenerTotalIngresos();
        int completadasAntes = ventaRepository.contarVentasCompletadas();
        long efectivoAntes = ventaRepository.contarVentasPorMetodoPago("EFECTIVO");

        ventaRepository.saveAndFlush(crearVenta(cliente.getIdCliente(), usuario.getIdUsuario(), producto.getIdProducto(), 25.0, "EFECTIVO", "COMPLETADA", "V-001", new Date()));
        ventaRepository.saveAndFlush(crearVenta(cliente.getIdCliente(), usuario.getIdUsuario(), producto.getIdProducto(), 10.0, "YAPE", "CANCELADA", "V-002", new Date()));

        assertEquals(ingresosAntes + 25.0, ventaRepository.obtenerTotalIngresos());
        assertEquals(completadasAntes + 1, ventaRepository.contarVentasCompletadas());
        assertEquals(efectivoAntes + 1L, ventaRepository.contarVentasPorMetodoPago("EFECTIVO"));

        List<Object[]> top = ventaRepository.obtenerTopProductosConCategoria(PageRequest.of(0, 1000));
        assertTrue(top.stream().anyMatch(fila -> "Paracetamol".equals(fila[0])
                && ((Number) fila[1]).longValue() >= 1L
                && "Analgesicos".equals(fila[2])));
    }

    @Test
    void listarVentasConNombres_and_listarMisComprasRealizadas_returnJoinedRows() {
        Cliente cliente = clienteRepository.save(crearCliente("Maria Gomez"));
        Usuario usuario = usuarioRepository.save(crearUsuario("user-history"));
        Categoria categoria = categoriaRepository.save(crearCategoria("Vitaminas"));
        Producto producto = productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-V2", "Vitamina C"));

        ventaRepository.saveAndFlush(crearVenta(cliente.getIdCliente(), usuario.getIdUsuario(), producto.getIdProducto(), 18.0, "TARJETA", "COMPLETADA", "V-010", new Date()));

        List<Object[]> historial = ventaRepository.listarVentasConNombres();
        assertFalse(historial.isEmpty());
        assertTrue(historial.stream().anyMatch(fila -> "Maria Gomez".equals(fila[10]) && "Vitamina C".equals(fila[11])));

        List<Object[]> misCompras = ventaRepository.listarMisComprasRealizadas(usuario.getIdUsuario());
        assertTrue(misCompras.stream().anyMatch(fila -> "V-010".equals(fila[0])));
    }

    private Cliente crearCliente(String nombreCompleto) {
        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(nombreCompleto);
        cliente.setDocumentoIdentidad("DOC-" + nombreCompleto.hashCode());
        cliente.setCorreo(nombreCompleto.toLowerCase().replace(' ', '.') + "@mail.com");
        return cliente;
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
        usuario.setRol("ADMIN");
        return usuario;
    }

    private Producto crearProducto(int idCategoria, String sku, String nombre) {
        Producto producto = new Producto();
        producto.setIdCategoria(idCategoria);
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(10);
        producto.setStockMinimo(2);
        producto.setPrecio(5.0);
        return producto;
    }

    private Venta crearVenta(int idCliente, int idUsuario, int idProducto, double total, String metodoPago, String estado, String comprobante, Date fecha) {
        Venta venta = new Venta();
        venta.setIdCliente(idCliente);
        venta.setIdUsuario(idUsuario);
        venta.setIdProducto(idProducto);
        venta.setTotal(total);
        venta.setMetodoPago(metodoPago);
        venta.setEstado(estado);
        venta.setNroComprobante(comprobante);
        venta.setFecha(fecha);
        venta.setNroSerie("SER-001");
        return venta;
    }
}