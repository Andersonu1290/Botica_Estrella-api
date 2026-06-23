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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            // 🔥 1. ESTA ES LA LÍNEA VITAL QUE FALTABA: Le dice a Spring Security que respete el CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
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

    // 🔥 2. ESTE ES EL PERMISO VIP PARA NETLIFY: Se asegura de que no pida token en peticiones OPTIONS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "https://boticaestrella.netlify.app"
        ));
        // Aquí autorizamos explícitamente el método OPTIONS (El que está causando tu error rojo)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}