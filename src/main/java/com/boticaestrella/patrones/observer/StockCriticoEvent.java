package com.boticaestrella.patrones.observer;

/**
 * Evento inmutable que transporta la información de alertas para la botica Estrella.
 * Emplea la estructura nativa Record de Java 21 para omitir código repetitivo.
 */
public record StockCriticoEvent(String sku, int stockActual) {
}
