package com.boticaestrella.patrones.factory;

import org.springframework.stereotype.Component;

@Component("boleta") // Nombre clave para el mapa de Spring
public class Boleta implements IComprobante {
    @Override
    public String generar(String datosVenta) {
        return "=========================================\n" +
               "   [SISTEMA boticaestrella] - BOLETA EMITIDA  \n" +
               "   DATOS: " + datosVenta + "\n" +
               "=========================================";
    }
}
