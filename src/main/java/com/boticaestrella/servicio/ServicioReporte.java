package com.boticaestrella.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;
import com.boticaestrella.repository.VentaRepository;
import com.boticaestrella.repository.VentaClienteRepository;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.modelo.VentaCliente;

@Service
public class ServicioReporte implements IGeneraReporte {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final VentaClienteRepository ventaClienteRepository; 
    private final SeriesRepository seriesRepository;

    public ServicioReporte(ProductoRepository productoRepository, 
                           VentaRepository ventaRepository, 
                           VentaClienteRepository ventaClienteRepository,
                           SeriesRepository seriesRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.ventaClienteRepository = ventaClienteRepository;
        this.seriesRepository = seriesRepository;
    }

    @Override
    public Map<String, Integer> generarResumenEjecutivo() {
        Map<String, Integer> kpis = new HashMap<>();

        kpis.put("totalStock", productoRepository.obtenerTotalUnidadesStock());
        
        int ventasPos = ventaRepository.contarVentasCompletadas();
        int ventasWeb = ventaClienteRepository.contarVentasCompletadasWeb();
        kpis.put("totalVentas", ventasPos + ventasWeb);

        kpis.put("totalMermas", (int) seriesRepository.countByEstado("MERMA"));
        kpis.put("stockCritico", productoRepository.contarStockCritico());

        return kpis;
    }

    public double obtenerIngresosTotales() { 
        double ingresosPos = ventaRepository.obtenerTotalIngresos();
        double ingresosWeb = ventaClienteRepository.obtenerTotalIngresosWeb();
        return ingresosPos + ingresosWeb;
    }

    public String[] obtenerTopProductos() { 
        List<Object[]> topPos = ventaRepository.obtenerTopProductosConCategoria(PageRequest.of(0, 100));
        List<Object[]> topWeb = ventaClienteRepository.obtenerTopProductosWeb();
        
        Map<String, ProductoKpi> consolidado = new HashMap<>();

        for (Object[] fila : topPos) {
            String prod = (String) fila[0];
            int cant = ((Number) fila[1]).intValue();
            String cat = fila[2] != null ? (String) fila[2] : "Sin Categoría";
            consolidado.put(prod, new ProductoKpi(prod, cant, cat));
        }

        for (Object[] fila : topWeb) {
            String prod = (String) fila[0];
            int cant = ((Number) fila[1]).intValue();
            String cat = fila[2] != null ? (String) fila[2] : "Sin Categoría";
            
            if (consolidado.containsKey(prod)) {
                consolidado.get(prod).cantidad += cant;
            } else {
                consolidado.put(prod, new ProductoKpi(prod, cant, cat));
            }
        }

        return consolidado.values().stream()
                .sorted((a, b) -> Integer.compare(b.cantidad, a.cantidad))
                .limit(5)
                .map(k -> k.nombre + "||" + k.cantidad + "||" + k.categoria)
                .toArray(String[]::new);
    }

    public String[] obtenerStockCategoria() { 
        List<Object[]> resultados = productoRepository.obtenerStockPorCategoria();
        String[] stockPorCategoria = new String[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            Object[] fila = resultados.get(i);
            String categoria = (String) fila[0];
            Number stock = fila[1] != null ? (Number) fila[1] : 0; 
            stockPorCategoria[i] = categoria + "||" + stock;
        }
        return stockPorCategoria; 
    }

    // 🔥 NUEVO MÉTODO: UNIFICA Y ORDENA LAS ÚLTIMAS 5 VENTAS (CAJA + WEB)
    public List<Venta> obtenerAuditoriaGlobal() {
        // 1. Obtener ventas físicas (POS)
        List<Venta> ventasPos = ventaRepository.listarVentasConNombres().stream().map(r -> {
            Venta v = new Venta();
            v.setNroComprobante((String) r[5]);
            v.setNombreCliente((String) r[10]);
            v.setNombreProducto((String) r[11]);
            v.setTotal(((Number) r[7]).doubleValue());
            v.setEstado((String) r[9]);
            
            Object f = r[8];
            if (f instanceof java.time.LocalDateTime localDateTime) {
                v.setFecha(java.sql.Timestamp.valueOf(localDateTime));
            } else if (f instanceof java.sql.Timestamp timestamp) {
                v.setFecha((java.sql.Timestamp) timestamp);
            } else {
                v.setFecha((java.util.Date) f);
            }
            return v;
        }).collect(Collectors.toList());

        // 2. Obtener ventas Web (E-commerce)
        List<Venta> ventasWeb = ventaClienteRepository.findAll().stream().map(vc -> {
            Venta v = new Venta();
            v.setNroComprobante(vc.getNroPedido());
            v.setNombreCliente(vc.getNombreCliente());
            v.setNombreProducto("Varios (Web)"); // El E-commerce tiene múltiples productos, lo resumimos
            v.setTotal(vc.getTotal());
            v.setEstado(vc.getEstado());
            v.setFecha(java.sql.Timestamp.valueOf(vc.getFechaPedido()));
            return v;
        }).collect(Collectors.toList());

        // 3. Unir, ordenar por fecha descendente y tomar las últimas 5
        List<Venta> consolidadas = new ArrayList<>();
        consolidadas.addAll(ventasPos);
        consolidadas.addAll(ventasWeb);

        consolidadas.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        return consolidadas.stream().limit(5).collect(Collectors.toList());
    }

    // Clase auxiliar
    class ProductoKpi {
        String nombre; int cantidad; String categoria;
        public ProductoKpi(String nombre, int cantidad, String categoria) {
            this.nombre = nombre; this.cantidad = cantidad; this.categoria = categoria;
        }
    }
}