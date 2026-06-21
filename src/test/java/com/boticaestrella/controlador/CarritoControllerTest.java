package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import com.boticaestrella.modelo.CarritoItem;
import com.boticaestrella.servicio.ServicioCarrito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CarritoControllerTest {

    @Mock
    private ServicioCarrito servicioCarrito;

    @InjectMocks
    private CarritoController carritoController;

    @Test
    void verCarrito_returnsItems() {
        CarritoItem item = new CarritoItem();
        item.setIdUsuario(1);
        when(servicioCarrito.obtenerCarritoDeUsuario(1)).thenReturn(List.of(item));

        ResponseEntity<List<CarritoItem>> resp = carritoController.verCarrito(1);
        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void agregarItem_callsService() {
        Map<String, Integer> payload = Map.of("idUsuario", 2, "idProducto", 5, "cantidad", 3);
        ResponseEntity<?> resp = carritoController.agregarItem(payload);
        verify(servicioCarrito).agregarOActualizarItem(2, 5, 3);
        assertEquals(200, resp.getStatusCode().value());
    }

    @Test
    void eliminarItem_callsService() {
        ResponseEntity<?> resp = carritoController.eliminarItem(9);
        verify(servicioCarrito).eliminarItem(9);
        assertEquals(200, resp.getStatusCode().value());
    }

    @Test
    void vaciarCarrito_callsService() {
        ResponseEntity<?> resp = carritoController.vaciarCarrito(7);
        verify(servicioCarrito).vaciarCarrito(7);
        assertEquals(200, resp.getStatusCode().value());
    }
}

