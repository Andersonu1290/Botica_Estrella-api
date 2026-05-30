package com.boticaestrella.servicio;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ServicioUsuario {

    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor (Recomendado en Spring)
    public ServicioUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario validarAcceso(String user, String pass) {
        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            return null;
        }
        // Usamos el Optional que definiste en Fase 3
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndPassword(user, pass);
        return usuarioOpt.orElse(null);
    }

    public List<Usuario> obtenerListaPersonal() {
        return usuarioRepository.findAll(); // Método nativo de JpaRepository
    }

    public boolean registrarNuevoPersonal(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return false; // El usuario ya existe
        }
        usuarioRepository.save(usuario); // Método nativo
        return true;
    }
}