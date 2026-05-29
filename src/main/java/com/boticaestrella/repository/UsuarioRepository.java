package com.boticaestrella.repository;

import com.boticaestrella.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Equivale a tu antiguo validarLogin
    Optional<Usuario> findByUsernameAndPassword(String username, String password);
    
    // Por si necesitas buscar si un usuario ya existe al registrar
    boolean existsByUsername(String username);
}