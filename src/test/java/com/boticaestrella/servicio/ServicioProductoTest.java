package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioProductoTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private ServicioProducto servicioProducto;

    @Captor
    private ArgumentCaptor<List<Series>> seriesListCaptor;

    @Test
    void testRegistrarNuevoProducto_savesProduct() {
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setIdProducto(1);
            return p;
        });

        boolean res = servicioProducto.registrarNuevoProducto("SKU123", "Nombre");
        assertTrue(res);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void testGuardarProducto_new_generatesSeries() {
        Producto p = new Producto();
        p.setIdProducto(0);
        p.setCodigoSKU("SKU-A");
        p.setStockActual(3);

        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto saved = inv.getArgument(0);
            saved.setIdProducto(42);
            return saved;
        });

        boolean res = servicioProducto.guardarProducto(p);

        assertTrue(res);
        verify(seriesRepository).saveAll(seriesListCaptor.capture());
        List<Series> listas = seriesListCaptor.getValue();
        assertEquals(3, listas.size());
        for (Series s : listas) {
            assertEquals(42, s.getIdProducto());
            assertEquals("DISPONIBLE", s.getEstado());
            assertTrue(s.getNumeroSerie().startsWith("SKU-A-"));
        }
    }

}

