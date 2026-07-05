package com.boticaestrella.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.modelo.Producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void findByCodigoSKU_returnsSavedProduct() {
        Categoria categoria = categoriaRepository.save(crearCategoria("Higiene"));
        Producto producto = productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-100", "Alcohol", 12, 3, 8.5));

        Producto encontrado = productoRepository.findByCodigoSKU("SKU-100").orElseThrow();

        assertEquals(producto.getIdProducto(), encontrado.getIdProducto());
        assertEquals("Alcohol", encontrado.getNombre());
    }

    @Test
    void stockQueries_returnExpectedResults() {
        String sufijo = UUID.randomUUID().toString();
        Categoria categoria = categoriaRepository.save(crearCategoria("Cuidado-" + sufijo));

        int stockAntes = productoRepository.obtenerTotalUnidadesStock();
        int criticoAntes = productoRepository.contarStockCritico();
        long agotadosAntes = productoRepository.contarProductosAgotados();

        Producto jabon = productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-200-" + sufijo, "Jabon-" + sufijo, 5, 3, 4.0));
        Producto shampoo = productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-201-" + sufijo, "Shampoo-" + sufijo, 2, 4, 7.0));

        assertEquals(stockAntes + 7, productoRepository.obtenerTotalUnidadesStock());
        assertEquals(criticoAntes + 1, productoRepository.contarStockCritico());
        assertEquals(1L, productoRepository.buscarPorNombreOSku(jabon.getCodigoSKU()).size());
        assertEquals(agotadosAntes, productoRepository.contarProductosAgotados());

        List<Object[]> stockPorCategoria = productoRepository.obtenerStockPorCategoria();
        assertTrue(stockPorCategoria.stream().anyMatch(fila -> categoria.getNombre().equals(fila[0])
                && ((Number) fila[1]).longValue() == 7L));
    }

    @Test
    void valorInventario_returnsSumOfPriceTimesStock() {
        String sufijo = UUID.randomUUID().toString();
        Categoria categoria = categoriaRepository.save(crearCategoria("General-" + sufijo));
        double inventarioAntes = productoRepository.obtenerValorTotalInventario();

        productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-300-" + sufijo, "Producto A-" + sufijo, 4, 1, 10.0));
        productoRepository.save(crearProducto(categoria.getIdCategoria(), "SKU-301-" + sufijo, "Producto B-" + sufijo, 6, 1, 5.0));

        assertEquals(inventarioAntes + 70.0, productoRepository.obtenerValorTotalInventario());
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Producto crearProducto(int idCategoria, String sku, String nombre, int stockActual, int stockMinimo, double precio) {
        Producto producto = new Producto();
        producto.setIdCategoria(idCategoria);
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(stockActual);
        producto.setStockMinimo(stockMinimo);
        producto.setPrecio(precio);
        return producto;
    }
}