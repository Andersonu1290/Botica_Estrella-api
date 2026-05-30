package com.boticaestrella.controlador;

import com.boticaestrella.dto.ReporteDashboardDTO;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioReporte;
import com.boticaestrella.servicio.ServicioVenta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@CrossOrigin(origins = "*") // Permite la comunicación segura con tu frontend
public class ReporteController {

    private final ServicioReporte servicioReporte;
    private final ServicioVenta servicioVenta;

    // Inyección de dependencias por constructor
    public ReporteController(ServicioReporte servicioReporte, ServicioVenta servicioVenta) {
        this.servicioReporte = servicioReporte;
        this.servicioVenta = servicioVenta;
    }

    /**
     * Endpoint: GET /api/v1/reportes/dashboard
     * Reemplaza por completo el doGet de tu antiguo ReporteServlet.
     * Consolida las métricas comerciales, gráficos y el historial en un único JSON limpio.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ReporteDashboardDTO> obtenerDashboardCompleto() {
        // 1. Obtener KPIs ejecutivos (Stock total, mermas, stock crítico)
        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();

        // 2. Obtener flujos de ingresos reales y métricas de gráficos
        double ingresos = servicioReporte.obtenerIngresosTotales();
        String[] topProd = servicioReporte.obtenerTopProductos();
        String[] catStock = servicioReporte.obtenerStockCategoria();

        // 3. Obtener el historial de ventas vinculadas (Antiguo listado de VentaDAO)
        List<Venta> ultimasVentas = servicioVenta.obtenerHistorialVentas();

        // 4. Construimos la respuesta unificada usando el DTO
        ReporteDashboardDTO dashboard = new ReporteDashboardDTO(kpis, ingresos, topProd, catStock, ultimasVentas);

        return ResponseEntity.ok(dashboard);
    }
}
