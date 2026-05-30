package com.boticaestrella.servicio;

import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.VentaRepository;
import com.boticaestrella.repository.SeriesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioReporte implements IGeneraReporte {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final SeriesRepository seriesRepository;

    // Inyección de dependencias por constructor
    public ServicioReporte(ProductoRepository productoRepository, 
                           VentaRepository ventaRepository, 
                           SeriesRepository seriesRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.seriesRepository = seriesRepository;
    }

    @Override
    public Map<String, Integer> generarResumenEjecutivo() {
        Map<String, Integer> kpis = new HashMap<>();

        // 1. Total Stock: Ahora suma las unidades físicas reales
        int totalUnidades = productoRepository.obtenerTotalUnidadesStock();
        kpis.put("totalStock", totalUnidades);

        // 2. Total Ventas: Solo las salidas procesadas (excluye anuladas)
        int totalVentasCompletadas = ventaRepository.contarVentasCompletadas();
        kpis.put("totalVentas", totalVentasCompletadas);

        // 3. Total Mermas: Cuenta estrictamente los equipos defectuosos
        int totalMermas = (int) seriesRepository.countByEstado("MERMA"); 
        kpis.put("totalMermas", totalMermas);

        // 4. Stock Crítico: Valida contra la columna "stock_minimo"
        int stockCriticoCount = productoRepository.contarStockCritico();
        kpis.put("stockCritico", stockCriticoCount);

        return kpis;
    }

    /**
     * Recupera el total de flujos monetarios acumulados por transacciones exitosas
     */
    public double obtenerIngresosTotales() { 
        // Corregido: cambiamos 'obtainTotalIngresos' por 'obtenerTotalIngresos'
        return ventaRepository.obtenerTotalIngresos(); 
    }


    /**
     * Obtiene los productos más demandados de la botica utilizando Pageable 
     * para evitar alertas de sintaxis nativa SQL en editores como VS Code.
     */
    public String[] obtenerTopProductos() { 
        // Limitamos la consulta a los 5 productos más vendidos de forma segura
        Pageable topFive = PageRequest.of(0, 5);
        List<Object[]> resultados = ventaRepository.obtenerProductosMasVendidos(topFive);
        
        String[] topProductos = new String[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            Object[] fila = resultados.get(i);
            // Convierte el nombre del producto y su conteo en un string legible
            topProductos[i] = fila[0] + " (Vendidos: " + fila[1] + ")";
        }
        return topProductos;
    }

    /**
     * Mapeo auxiliar de stock consolidado por categorías de la botica
     */
    public String[] obtenerStockCategoria() { 
        // Aquí puedes invocar una consulta mapeada o un flujo de datos consolidado similar
        return new String[]{"Medicamentos: 120", "Cuidado Personal: 45", "Material Médico: 15"}; 
    }
}
