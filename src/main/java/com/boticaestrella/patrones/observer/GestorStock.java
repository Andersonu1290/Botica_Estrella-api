package com.boticaestrella.patrones.observer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GestorStock {

    private final ApplicationEventPublisher eventPublisher;

    // Spring Boot inyecta de forma automática el publicador de eventos nativo
    public GestorStock(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Reemplaza a tu antiguo método "notificarObservadores".
     * Lanza el evento al contenedor de Spring para que todos los métodos @EventListener
     * reaccionen inmediatamente de forma síncrona y desacoplada.
     */
    public void dispararAlertaStockCritico(String sku, int stockActual) {
        StockCriticoEvent evento = new StockCriticoEvent(sku, stockActual);
        eventPublisher.publishEvent(evento);
    }
}
