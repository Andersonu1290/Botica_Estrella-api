package com.boticaestrella.patrones.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LogisticaObserver {

    // Inicialización del responsable compatible con componentes de Spring
    private final String nombreResponsable = "Jefe de Almacén";

    /**
     * Intercepta el evento transmitido y ejecuta la lógica de advertencia.
     */
    @EventListener
    public String procesarAlertaStock(StockCriticoEvent evento) {
        String mensaje = "El componente SKU: " + evento.sku() + 
                         " ha alcanzado el nivel crítico de reposición. Stock actual: " + evento.stockActual() + " und.";
        
        String logResultado = "[ALERTA LOGÍSTICA: " + nombreResponsable + "] -> " + mensaje;
        System.out.println(logResultado);
        
        return logResultado;
    }
}
