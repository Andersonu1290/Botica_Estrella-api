package com.boticaestrella.patrones.adapter;

import org.springframework.stereotype.Service;

@Service
public class PagoAdapter implements IServicioPago {
    
    private final PasarelaExterna pasarela;

    // Spring Boot inyecta automáticamente la instancia del componente PasarelaExterna
    public PagoAdapter(PasarelaExterna pasarela) {
        this.pasarela = pasarela;
    }

    @Override
    public String procesarPago(double monto) {
        return "[ADAPTER] Traduciendo... " + pasarela.realizarTransaccion(monto);
    }
}
