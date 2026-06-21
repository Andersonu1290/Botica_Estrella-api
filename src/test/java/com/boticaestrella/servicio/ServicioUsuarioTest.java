package com.boticaestrella.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioUsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ServicioUsuario servicioUsuario;

    @Test
    void testValidarAcceso_success() {
        Usuario u = new Usuario();
        u.setUsername("juan");
        u.setPassword("pwd");

        when(usuarioRepository.findByUsernameAndPassword("juan", "pwd")).thenReturn(Optional.of(u));

        Usuario res = servicioUsuario.validarAcceso("juan", "pwd");
        assertNotNull(res);
        assertEquals("juan", res.getUsername());
    }

    @Test
    void testValidarAcceso_invalid_returnsNull() {
        when(usuarioRepository.findByUsernameAndPassword("x", "y")).thenReturn(Optional.empty());
        assertNull(servicioUsuario.validarAcceso("x", "y"));
    }

    @Test
    void testRegistrarNuevoPersonal_existingUsername_returnsFalse() {
        Usuario u = new Usuario();
        u.setUsername("exists");
        when(usuarioRepository.existsByUsername("exists")).thenReturn(true);

        boolean ok = servicioUsuario.registrarNuevoPersonal(u);
        assertFalse(ok);
        verify(usuarioRepository, never()).save(any());
    }

}

