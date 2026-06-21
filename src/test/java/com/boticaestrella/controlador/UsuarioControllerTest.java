package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.servicio.ServicioUsuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private ServicioUsuario servicioUsuario;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void listarPersonal_returnsList() {
        when(servicioUsuario.obtenerListaPersonal()).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> resp = usuarioController.listarPersonal();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void registrarUsuario_missingFields_returnsBadRequest() {
        Usuario u = new Usuario();
        ResponseEntity<?> resp = usuarioController.registrarUsuario(u);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void registrarClienteDesdeTienda_setsRoleAndDelegates() {
        Usuario u = new Usuario();
        u.setUsername("c");
        u.setPassword("p");
        when(servicioUsuario.registrarNuevoPersonal(any())).thenReturn(true);
        ResponseEntity<?> resp = usuarioController.registrarClienteDesdeTienda(u);
        assertEquals(201, resp.getStatusCode().value());
        assertEquals("CLIENTE", u.getRol());
    }
}

