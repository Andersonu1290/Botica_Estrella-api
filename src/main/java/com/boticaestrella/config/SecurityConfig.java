package com.boticaestrella.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (Indispensable para APIs REST y Client-Side Rendering)
            .csrf(csrf -> csrf.disable())
            
            // 2. Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Permitir que el frontend (HTML, CSS, JS e imágenes) cargue sin pedir contraseña
                .requestMatchers("/**/*.html", "/assets/**").permitAll()
                
                // Permitir que CUALQUIERA intente iniciar sesión en tu API
                .requestMatchers("/api/v1/auth/login").permitAll()
                
                // TEMPORAL: Mientras no activemos JWT (Fase 7), dejamos pasar las peticiones a la API
                // para que puedas probar tu carrito de ventas y tu inventario hoy mismo.
                .anyRequest().permitAll() 
            );

        return http.build();
    }
}