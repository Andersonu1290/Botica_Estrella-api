package com.boticaestrella.controlador;

import com.boticaestrella.dto.ReporteDashboardDTO;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioReporte;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@CrossOrigin(origins = "*") 
public class ReporteController {

    private final ServicioReporte servicioReporte;

    public ReporteController(ServicioReporte servicioReporte) {
        this.servicioReporte = servicioReporte;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ReporteDashboardDTO> obtenerDashboardCompleto() {
        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();
        double ingresos = servicioReporte.obtenerIngresosTotales();
        String[] topProd = servicioReporte.obtenerTopProductos();
        String[] catStock = servicioReporte.obtenerStockCategoria();

        // 🔥 NUEVO: Traemos la auditoría fusionada de Tienda Física + Web
        List<Venta> ultimasVentas = servicioReporte.obtenerAuditoriaGlobal();

        ReporteDashboardDTO dashboard = new ReporteDashboardDTO(kpis, ingresos, topProd, catStock, ultimasVentas);
        return ResponseEntity.ok(dashboard);
    }
}