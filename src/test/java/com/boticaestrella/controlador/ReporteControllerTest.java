package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import com.boticaestrella.dto.ReporteDashboardDTO;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioReporte;
import com.boticaestrella.servicio.ServicioVenta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ReporteControllerTest {

    @Mock
    private ServicioReporte servicioReporte;

    @Mock
    private ServicioVenta servicioVenta;

    @InjectMocks
    private ReporteController reporteController;

    @Test
    void obtenerDashboardCompleto_returnsDTO() {
        when(servicioReporte.generarResumenEjecutivo()).thenReturn(Map.of("totalStock", 10));
        when(servicioReporte.obtenerIngresosTotales()).thenReturn(100.0);
        when(servicioReporte.obtenerTopProductos()).thenReturn(new String[]{});
        when(servicioReporte.obtenerStockCategoria()).thenReturn(new String[]{});
        when(servicioVenta.obtenerHistorialVentas()).thenReturn(List.of(new Venta()));

        ResponseEntity<ReporteDashboardDTO> resp = reporteController.obtenerDashboardCompleto();
        assertEquals(200, resp.getStatusCode().value());
        ReporteDashboardDTO dto = resp.getBody();
        assertNotNull(dto);
        assertEquals(10, dto.getKpis().get("totalStock").intValue());
    }
}

