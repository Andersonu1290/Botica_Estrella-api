package com.boticaestrella.servicio;

import com.boticaestrella.modelo.Usuario;
import com.boticaestrella.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ServicioUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias por constructor (Recomendado en Spring)
    @Autowired
    public ServicioUsuario(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        // En entornos de análisis estático el bean podría no resolverse; usamos un fallback seguro.
        this.passwordEncoder = passwordEncoder != null ? passwordEncoder : new BCryptPasswordEncoder();
    }

    public Usuario validarAcceso(String user, String pass) {
        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            return null;
        }
        // Buscamos por username y comparamos el hash con BCrypt
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(user);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (passwordEncoder.matches(pass, u.getPassword())) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> obtenerListaPersonal() {
        return usuarioRepository.findAll(); // Método nativo de JpaRepository
    }

    public boolean registrarNuevoPersonal(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return false; // El usuario ya existe
        }
        // Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario); // Método nativo
        return true;
    }
}