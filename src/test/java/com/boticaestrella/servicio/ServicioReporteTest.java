package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;
import com.boticaestrella.repository.VentaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioReporteTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private ServicioReporte servicioReporte;

    @Test
    void testGenerarResumenEjecutivo_returnsKPIs() {
        when(productoRepository.obtenerTotalUnidadesStock()).thenReturn(100);
        when(ventaRepository.contarVentasCompletadas()).thenReturn(25);
        when(seriesRepository.countByEstado("MERMA")).thenReturn(3L);
        when(productoRepository.contarStockCritico()).thenReturn(7);

        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();

        assertEquals(100, kpis.get("totalStock"));
        assertEquals(25, kpis.get("totalVentas"));
        assertEquals(3, kpis.get("totalMermas"));
        assertEquals(7, kpis.get("stockCritico"));
    }

    @Test
    void testObtenerIngresosTotales_callsRepository() {
        when(ventaRepository.obtenerTotalIngresos()).thenReturn(1234.56);
        double ingreso = servicioReporte.obtenerIngresosTotales();
        assertEquals(1234.56, ingreso);
    }

}

