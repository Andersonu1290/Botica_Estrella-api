package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Optional;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;
import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.repository.KardexRepository;
import com.boticaestrella.repository.MermaRepository;
import com.boticaestrella.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MermaServiceTest {

    @Mock
    private MermaRepository mermaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private KardexRepository kardexRepository;

    @InjectMocks
    private MermaService mermaService;

    @Captor
    private ArgumentCaptor<Series> seriesCaptor;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    @Captor
    private ArgumentCaptor<MovimientoKardex> kardexCaptor;

    @Test
    void testProcesarMerma_success() throws Exception {
        String nroSerie = "SN-123";

        Series serie = new Series();
        serie.setNumeroSerie(nroSerie);
        serie.setEstado("DISPONIBLE");
        serie.setIdProducto(5);

        Producto producto = new Producto();
        producto.setIdProducto(5);
        producto.setStockActual(10);

        when(mermaRepository.findByNumeroSerieAndEstado(nroSerie, "DISPONIBLE"))
                .thenReturn(Optional.of(serie));

        when(productoRepository.findById(5)).thenReturn(Optional.of(producto));

        mermaService.procesarMerma(nroSerie, "Caducado", 2);

        verify(mermaRepository).save(seriesCaptor.capture());
        Series savedSeries = seriesCaptor.getValue();
        assertEquals("MERMA", savedSeries.getEstado());

        verify(productoRepository).save(productoCaptor.capture());
        Producto savedProducto = productoCaptor.getValue();
        assertEquals(9, savedProducto.getStockActual());

        verify(kardexRepository).save(kardexCaptor.capture());
        MovimientoKardex mov = kardexCaptor.getValue();
        assertEquals(5, mov.getIdProducto());
        assertEquals("MERMA", mov.getTipoMovimiento());
        assertEquals(1, mov.getCantidad());
        assertTrue(mov.getMotivo().contains(nroSerie));
    }
}

