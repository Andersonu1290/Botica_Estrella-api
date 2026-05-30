package com.boticaestrella.dto;

import java.util.List;
import java.util.Map;

import com.boticaestrella.modelo.Venta;

public class ReporteDashboardDTO {

    private Map<String, Integer> kpis;
    private double ingresosTotales;
    private String[] topProd;
    private String[] catStock;
    private List<Venta> ultimasVentas;

    public ReporteDashboardDTO(
            Map<String, Integer> kpis,
            double ingresosTotales,
            String[] topProd,
            String[] catStock,
            List<Venta> ultimasVentas
    ) {
        this.kpis = kpis;
        this.ingresosTotales = ingresosTotales;
        this.topProd = topProd;
        this.catStock = catStock;
        this.ultimasVentas = ultimasVentas;
    }

    public Map<String, Integer> getKpis() {
        return kpis;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public String[] getTopProd() {
        return topProd;
    }

    public String[] getCatStock() {
        return catStock;
    }

    public List<Venta> getUltimasVentas() {
        return ultimasVentas;
    }
}