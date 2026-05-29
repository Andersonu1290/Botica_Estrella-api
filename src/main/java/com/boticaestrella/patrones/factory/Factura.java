package com.boticaestrella.patrones.factory;

import org.springframework.stereotype.Component;

@Component("factura") // Nombre clave para el mapa de Spring
public class Factura implements IComprobante {
    @Override
    public String generar(String datosVenta) {
        return "=========================================\n" +
               "  [SISTEMA boticaestrella] - FACTURA EMITIDA  \n" +
               "  RUC CLIENTE / IGV DESGLOSADO APLICADO  \n" +
               "  DATOS: " + datosVenta + "\n" +
               "=========================================";
    }
}
