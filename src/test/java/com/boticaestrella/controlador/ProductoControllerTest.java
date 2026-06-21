package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.servicio.ServicioProducto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    @Mock
    private ServicioProducto servicioProducto;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void listarProductos_returnsList() {
        when(servicioProducto.obtenerInventarioActivo()).thenReturn(List.of(new Producto()));
        ResponseEntity<List<Producto>> resp = productoController.listarProductos();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void buscarPorId_notFound() {
        when(servicioProducto.buscarProducto(5)).thenReturn(null);
        ResponseEntity<?> resp = productoController.buscarPorId(5);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    void buscarPorId_found() {
        Producto p = new Producto();
        p.setIdProducto(3);
        when(servicioProducto.buscarProducto(3)).thenReturn(p);
        ResponseEntity<?> resp = productoController.buscarPorId(3);
        assertEquals(200, resp.getStatusCode().value());
        Producto body = (Producto) resp.getBody();
        assertEquals(3, body.getIdProducto());
    }

    @Test
    void eliminarProducto_returnsNotFound_whenServiceReturnsFalse() {
        when(servicioProducto.eliminarProducto(9)).thenReturn(false);
        ResponseEntity<?> resp = productoController.eliminarProducto(9);
        assertEquals(404, resp.getStatusCode().value());
    }
}

