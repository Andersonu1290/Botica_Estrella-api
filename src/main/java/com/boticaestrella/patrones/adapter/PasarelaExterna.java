package com.boticaestrella.patrones.adapter;

import org.springframework.stereotype.Component;

@Component
public class PasarelaExterna {
    
    public String realizarTransaccion(double total) {
        return "[API BANCO] Conexión segura. Transacción APROBADA por el monto de: S/ " + total;
    }
}
