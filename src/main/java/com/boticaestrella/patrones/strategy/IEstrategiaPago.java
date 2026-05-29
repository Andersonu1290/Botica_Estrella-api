package com.boticaestrella.patrones.strategy;

public interface IEstrategiaPago {
    String procesarPago(String comprobante, double monto);
}
