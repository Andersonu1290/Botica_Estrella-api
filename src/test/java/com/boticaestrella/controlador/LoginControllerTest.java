package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.servicio.ServicioUsuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private ServicioUsuario servicioUsuario;

    @InjectMocks
    private LoginController loginController;

    @Test
    void ingresar_successful_returnsUserWithoutPassword() {
        Usuario u = new Usuario();
        u.setUsername("ana");
        u.setPassword("secret");
        when(servicioUsuario.validarAcceso("ana", "secret")).thenReturn(u);

        ResponseEntity<?> resp = loginController.ingresar(Map.of("username", "ana", "password", "secret"));
        assertEquals(200, resp.getStatusCode().value());
        Usuario body = (Usuario) resp.getBody();
        assertNull(body.getPassword());
        assertEquals("ana", body.getUsername());
    }

    @Test
    void ingresar_missingParams_returnsBadRequest() {
        ResponseEntity<?> resp = loginController.ingresar(Map.of("username", "x"));
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void ingresar_invalidCredentials_returnsUnauthorized() {
        when(servicioUsuario.validarAcceso("u", "p")).thenReturn(null);
        ResponseEntity<?> resp = loginController.ingresar(Map.of("username", "u", "password", "p"));
        assertEquals(401, resp.getStatusCode().value());
    }

    @Test
    void salir_returnsOk() {
        ResponseEntity<?> resp = loginController.salir();
        assertEquals(200, resp.getStatusCode().value());
    }
}

