package com.boticaestrella.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.servicio.ServicioUsuario;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;

    // Inyección de dependencias por constructor
    public UsuarioController(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    /**
     * Endpoint: GET /api/v1/usuarios
     * Reemplaza la acción "listar" del doGet de tu antiguo Servlet.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarPersonal() {
        List<Usuario> usuarios = servicioUsuario.obtenerListaPersonal();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint: POST /api/v1/usuarios/registrar
     * Reemplaza por completo el bloque "registrar" del doPost del Servlet.
     * Consume un JSON con el payload del nuevo empleado y lo inserta si el username está disponible.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        try {
            // Validaciones rápidas de campos obligatorios para el entorno REST
            if (nuevoUsuario.getUsername() == null || nuevoUsuario.getPassword() == null || nuevoUsuario.getRol() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Faltan datos requeridos: username, password o rol."));
            }

            // Invocamos tu servicio el cual mapea al repositorio y valida duplicados
            boolean registrado = servicioUsuario.registrarNuevoPersonal(nuevoUsuario);

            if (registrado) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("mensaje", "Personal registrado de forma exitosa en el sistema."));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "El nombre de usuario '" + nuevoUsuario.getUsername() + "' ya se encuentra en uso."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ocurrió un error al procesar el registro: " + e.getMessage()));
        }
    }
}
