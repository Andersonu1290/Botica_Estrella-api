package com.boticaestrella.patrones.strategy;

import com.boticaestrella.patrones.adapter.IServicioPago;
import org.springframework.stereotype.Component;

@Component("pagoTarjeta") // Nombre clave para el mapa del patrón Strategy
public class PagoTarjeta implements IEstrategiaPago {
    
    // Inyección de la interfaz del adaptador (Desacoplamiento total)
    private final IServicioPago servicioPago;

    // El constructor recibe el componente gestionado por Spring
    public PagoTarjeta(IServicioPago servicioPago) {
        this.servicioPago = servicioPago;
    }

    @Override
    public String procesarPago(String comprobante, double monto) {
        // Ejecutamos la traducción y transacción segura a través del adaptador moderno
        String respuestaBanco = servicioPago.procesarPago(monto);
        
        return "Pago con TARJETA. " + respuestaBanco + " (Ticket: " + comprobante + ")";
    }
}
