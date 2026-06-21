package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.CarritoItem;
import com.boticaestrella.repository.CarritoRepository;
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
public class ServicioCarritoTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ServicioCarrito servicioCarrito;

    @Captor
    private ArgumentCaptor<CarritoItem> carritoItemCaptor;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations handled by MockitoExtension
    }

    @Test
    void testObtenerCarritoDeUsuario_populatesProducto() {
        CarritoItem item = new CarritoItem();
        item.setIdProducto(10);
        List<CarritoItem> lista = new ArrayList<>();
        lista.add(item);

        Producto producto = new Producto();
        producto.setIdProducto(10);
        producto.setNombre("Medicamento X");

        when(carritoRepository.findByIdUsuario(1)).thenReturn(lista);
        when(productoRepository.findById(10)).thenReturn(Optional.of(producto));

        List<CarritoItem> resultado = servicioCarrito.obtenerCarritoDeUsuario(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0).getProducto());
        assertEquals("Medicamento X", resultado.get(0).getProducto().getNombre());
    }

    @Test
    void testAgregarOActualizarItem_whenExiste_updatesCantidadAndSaves() {
        CarritoItem existente = new CarritoItem();
        existente.setIdUsuario(1);
        existente.setIdProducto(10);
        existente.setCantidad(2);

        when(carritoRepository.findByIdUsuarioAndIdProducto(1, 10)).thenReturn(Optional.of(existente));

        servicioCarrito.agregarOActualizarItem(1, 10, 3);

        // Verificamos que se guardó y la cantidad fue actualizada
        verify(carritoRepository).save(carritoItemCaptor.capture());
        CarritoItem guardado = carritoItemCaptor.getValue();
        assertEquals(5, guardado.getCantidad());
        assertEquals(1, guardado.getIdUsuario());
        assertEquals(10, guardado.getIdProducto());
    }

    @Test
    void testAgregarOActualizarItem_whenNoExiste_savesNewItem() {
        when(carritoRepository.findByIdUsuarioAndIdProducto(2, 20)).thenReturn(Optional.empty());

        servicioCarrito.agregarOActualizarItem(2, 20, 4);

        verify(carritoRepository).save(carritoItemCaptor.capture());
        CarritoItem nuevo = carritoItemCaptor.getValue();
        assertEquals(2, nuevo.getIdUsuario());
        assertEquals(20, nuevo.getIdProducto());
        assertEquals(4, nuevo.getCantidad());
    }

    @Test
    void testEliminarItem_callsDeleteById() {
        servicioCarrito.eliminarItem(99);
        verify(carritoRepository).deleteById(99);
    }

    @Test
    void testVaciarCarrito_callsDeleteByIdUsuario() {
        servicioCarrito.vaciarCarrito(7);
        verify(carritoRepository).deleteByIdUsuario(7);
    }
}

