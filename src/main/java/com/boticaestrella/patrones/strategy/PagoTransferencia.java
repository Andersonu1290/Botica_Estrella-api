package com.boticaestrella.patrones.strategy;

import org.springframework.stereotype.Component;

@Component("pagoTransferencia") // Nombre clave para Yape/Plin
public class PagoTransferencia implements IEstrategiaPago {
    @Override
    public String procesarPago(String comprobante, double monto) {
        return "Pago procesado vía YAPE/PLIN por un total de S/ " + monto + ". Ticket: " + comprobante;
    }
}
