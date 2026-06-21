package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import com.boticaestrella.modelo.Series;
import com.boticaestrella.repository.MermaRepository;
import com.boticaestrella.servicio.MermaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class MermaControllerTest {

    @Mock
    private MermaRepository mermaRepository;

    @Mock
    private MermaService mermaService;

    @InjectMocks
    private MermaController mermaController;

    @Test
    void listarSeriesPorEstado_mapsRows() {
        Object[] row = new Object[]{1, "SN1", 5, "DISPONIBLE", "Prod", "SKU"};
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        lista.add(row);
        when(mermaRepository.listarSeriesConProducto("DISPONIBLE")).thenReturn(lista);

        ResponseEntity<List<Series>> resp = mermaController.listarSeriesPorEstado("DISPONIBLE");
        assertEquals(200, resp.getStatusCode().value());
        List<Series> body = resp.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        Series s = body.get(0);
        assertEquals("Prod", s.getNombreProducto());
    }

    @Test
    void registrarMerma_valid_callsService() throws Exception {
        Map<String, Object> payload = Map.of("numeroSerie", "SN1", "motivo", "Caducado", "idUsuario", 2);
        doNothing().when(mermaService).procesarMerma("SN1", "Caducado", 2);

        ResponseEntity<?> resp = mermaController.registrarMerma(payload);
        assertEquals(200, resp.getStatusCode().value());
    }

    @Test
    void registrarMerma_missingParams_returnsBadRequest() {
        Map<String, Object> payload = Map.of("numeroSerie", "SN1");
        ResponseEntity<?> resp = mermaController.registrarMerma(payload);
        assertEquals(400, resp.getStatusCode().value());
    }
}

