package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.repository.KardexRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class KardexControllerTest {

    @Mock
    private KardexRepository kardexRepository;

    @InjectMocks
    private KardexController kardexController;

    @Test
    void obtenerHistorialKardex_mapsResults() {
        Object[] row = new Object[]{1, "SALIDA", 2, LocalDateTime.now(), "Motivo", "Prod", "User"};
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        lista.add(row);
        when(kardexRepository.listarHistorialKardex()).thenReturn(lista);

        ResponseEntity<List<MovimientoKardex>> resp = kardexController.obtenerHistorialKardex();
        assertEquals(200, resp.getStatusCode().value());
        List<MovimientoKardex> body = resp.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        MovimientoKardex k = body.get(0);
        assertEquals("SALIDA", k.getTipoMovimiento());
        assertEquals("Prod", k.getNombreProducto());
    }
}

