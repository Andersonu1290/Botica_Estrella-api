package com.boticaestrella.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.boticaestrella.dto.LoginRequestDTO; // <-- Agregamos la importación del DTO
import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.config.JwtUtil;
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

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginController loginController;

    @Test
    void ingresar_successful_returnsUserWithoutPassword() {
        Usuario u = new Usuario();
        u.setUsername("ana");
        u.setPassword("secret");
        when(servicioUsuario.validarAcceso("ana", "secret")).thenReturn(u);
        when(jwtUtil.generateToken("ana", null)).thenReturn("token-demo");

        // Usamos el DTO en lugar del Map
        LoginRequestDTO request = new LoginRequestDTO("ana", "secret");
        ResponseEntity<?> resp = loginController.ingresar(request);
        
        assertEquals(200, resp.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertNotNull(body);
        assertEquals("token-demo", body.get("token"));

        Usuario bodyUsuario = (Usuario) body.get("usuario");
        assertNull(bodyUsuario.getPassword());
        assertEquals("ana", bodyUsuario.getUsername());
    }

    @Test
    void ingresar_missingParams_returnsBadRequest() {
        // Simulamos que el password no se envió (viene como null)
        LoginRequestDTO request = new LoginRequestDTO("x", null);
        ResponseEntity<?> resp = loginController.ingresar(request);
        
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void ingresar_invalidCredentials_returnsUnauthorized() {
        when(servicioUsuario.validarAcceso("u", "p")).thenReturn(null);
        
        // Usamos el DTO
        LoginRequestDTO request = new LoginRequestDTO("u", "p");
        ResponseEntity<?> resp = loginController.ingresar(request);
        
        assertEquals(401, resp.getStatusCode().value());
    }

    @Test
    void salir_returnsOk() {
        ResponseEntity<?> resp = loginController.salir();
        assertEquals(200, resp.getStatusCode().value());
    }
}