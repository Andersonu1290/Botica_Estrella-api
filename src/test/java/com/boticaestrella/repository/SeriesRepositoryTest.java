package com.boticaestrella.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeriesRepositoryTest {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void seriesQueries_returnExpectedRows() {
        String sufijo = UUID.randomUUID().toString();
        Producto producto = productoRepository.save(crearProducto("SKU-SER-1-" + sufijo, "Antibiotico-" + sufijo));
        long disponiblesAntes = seriesRepository.countByEstado("DISPONIBLE");
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-001", "DISPONIBLE"));
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-002", "DISPONIBLE"));
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-003", "ASIGNADO"));

        assertEquals(2, seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE").size());
        assertTrue(seriesRepository.findByNumeroSerieAndEstado("SER-003", "ASIGNADO").isPresent());
        assertEquals(disponiblesAntes + 2, seriesRepository.countByEstado("DISPONIBLE"));
        assertEquals(3L, seriesRepository.countByIdProducto(producto.getIdProducto()));
    }

    @Test
    void eliminarSeriesExcedentes_removesExtraDisponibleSeries() {
        String sufijo = UUID.randomUUID().toString();
        Producto producto = productoRepository.save(crearProducto("SKU-SER-2-" + sufijo, "Vitaminas-" + sufijo));
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-101", "DISPONIBLE"));
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-102", "DISPONIBLE"));
        seriesRepository.save(crearSeries(producto.getIdProducto(), "SER-103", "DISPONIBLE"));

        seriesRepository.eliminarSeriesExcedentes(producto.getIdProducto());

        List<Series> disponibles = seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE");
        assertTrue(disponibles.size() <= 1);
    }

    private Producto crearProducto(String sku, String nombre) {
        Categoria categoria = categoriaRepository.save(crearCategoria("General"));
        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(5);
        producto.setStockMinimo(1);
        producto.setPrecio(9.5);
        return producto;
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Series crearSeries(int idProducto, String numeroSerie, String estado) {
        Series series = new Series();
        series.setIdProducto(idProducto);
        series.setNumeroSerie(numeroSerie);
        series.setEstado(estado);
        return series;
    }
}