package com.boticaestrella.patrones.factory;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ComprobanteFactory {
    
    // Spring recolecta de forma automática los componentes "boleta" y "factura"
    private final Map<String, IComprobante> comprobantes;

    public ComprobanteFactory(Map<String, IComprobante> comprobantes) {
        this.comprobantes = comprobantes;
    }

    /**
     * Resuelve y devuelve el tipo de comprobante de manera dinámica.
     * Elimina por completo los bloques condicionales if/else.
     */
    public IComprobante crearComprobante(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return null;
        }
        
        // Buscamos en el mapa convirtiendo a minúsculas ("BOLETA" -> "boleta")
        return comprobantes.get(tipo.trim().toLowerCase());
    }
}
