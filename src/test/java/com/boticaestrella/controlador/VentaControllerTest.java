package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import com.boticaestrella.dto.VentaRequestDTO;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioVenta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class VentaControllerTest {

    @Mock
    private ServicioVenta servicioVenta;

    @InjectMocks
    private VentaController ventaController;

    @Test
    void obtenerHistorial_delegatesToService() {
        when(servicioVenta.obtenerHistorialVentas()).thenReturn(java.util.List.of(new Venta()));
        ResponseEntity<List<Venta>> resp = ventaController.obtenerHistorial();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void procesarVenta_success_returnsCreated() throws Exception {
        VentaRequestDTO req = new VentaRequestDTO(1, null, null, "BOLETA", "EFECTIVO", 2, "DNI1", "Cli", "c@c.com", 10.0);
        when(servicioVenta.procesarSalidaProducto(anyInt(), any(), any(), anyInt(), any(), any(), any(), any(), anyDouble()))
                .thenReturn(java.util.Map.of("comprobante", "TCK-1"));

        ResponseEntity<?> resp = ventaController.procesarVenta(req);
        assertEquals(201, resp.getStatusCode().value());
    }

    @Test
    void anularVenta_success_returnsOk() throws Exception {
        when(servicioVenta.anularVenta(5, 2)).thenReturn(true);
        ResponseEntity<?> resp = ventaController.anularVenta(5, 2);
        assertEquals(200, resp.getStatusCode().value());
    }
}

