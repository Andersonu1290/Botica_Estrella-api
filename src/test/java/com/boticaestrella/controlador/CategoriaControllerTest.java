package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.repository.CategoriaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CategoriaControllerTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaController categoriaController;

    @Test
    void listarCategorias_returnsList() {
        when(categoriaRepository.findAll()).thenReturn(List.of(new Categoria()));
        ResponseEntity<List<Categoria>> resp = categoriaController.listarCategorias();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void eliminarCategoria_notFound_whenDoesNotExist() {
        when(categoriaRepository.existsById(3)).thenReturn(false);
        ResponseEntity<?> resp = categoriaController.eliminarCategoria(3);
        assertEquals(404, resp.getStatusCode().value());
    }

    @Test
    void eliminarCategoria_conflict_onDataIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("fk constraint")).when(categoriaRepository).deleteById(4);
        when(categoriaRepository.existsById(4)).thenReturn(true);
        ResponseEntity<?> resp = categoriaController.eliminarCategoria(4);
        assertEquals(409, resp.getStatusCode().value());
    }
}

