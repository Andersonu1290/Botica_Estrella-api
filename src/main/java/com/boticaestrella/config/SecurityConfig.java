package com.boticaestrella.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Frontend estático
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/assets/**",
                    "/**/*.html",
                    "/**/*.js",
                    "/**/*.css",
                    "/**/*.png",
                    "/**/*.jpg",
                    "/**/*.jpeg",
                    "/**/*.svg",
                    "/**/*.webp",
                    "/**/*.avif"
                ).permitAll()

                // Autenticación y registro público
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/usuarios/registro-tienda").permitAll()

                // Ecommerce público: catálogo, categorías e imágenes
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll()

                // Si sirves imágenes desde este endpoint
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/*/imagen").permitAll()

                // Rutas privadas del cliente
                .requestMatchers("/api/v1/carrito/**").authenticated()
                .requestMatchers("/api/v1/pedidos/**").authenticated()
                .requestMatchers("/api/v1/perfil/**").authenticated()

                // Admin
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers("/api/v1/usuarios/registrar").hasAnyRole("ADMIN", "ALMACEN")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}