package com.boticaestrella.patrones.strategy;

import org.springframework.stereotype.Component;

@Component("pagoEfectivo") // Nombre clave para el mapa de Spring
public class PagoEfectivo implements IEstrategiaPago {
    @Override
    public String procesarPago(String comprobante, double monto) {
        return "Pago procesado en Efectivo (Caja) por un total de S/ " + monto + ". Ticket: " + comprobante;
    }
}
