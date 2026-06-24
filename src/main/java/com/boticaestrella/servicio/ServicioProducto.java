package com.boticaestrella.servicio;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.Series;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.SeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioProducto implements IConsultaStock, IRegistroProducto {
    
    private final ProductoRepository productoRepository;
    private final SeriesRepository seriesRepository;

    // Inyección de dependencias por constructor
    public ServicioProducto(ProductoRepository productoRepository, SeriesRepository seriesRepository) {
        this.productoRepository = productoRepository;
        this.seriesRepository = seriesRepository;
    }

    @Override
    public List<Producto> obtenerInventarioActivo() {
        return productoRepository.findAll(); // Reemplaza pDao.listarProductos()
    }

    @Override
    @Transactional
    public boolean registrarNuevoProducto(String sku, String nombre) {
        Producto p = new Producto();
        p.setCodigoSKU(sku);
        p.setNombre(nombre);
        p.setIdCategoria(1);
        p.setStockActual(0);
        p.setStockMinimo(5);
        p.setPrecio(0.0);
        
        productoRepository.save(p);
        return true;
    }
    
    /**
     * Procesa inserciones y actualizaciones de productos controlando el stock.
     * @Transactional asegura que si el bucle de series falla, se cancela el guardado del producto.
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean guardarProducto(Producto p) {
        int diferenciaStock = 0;
        boolean esNuevo = (p.getIdProducto() == 0);

        if (esNuevo) {
            // Guardamos el producto y recuperamos la entidad con el ID autogenerado por MySQL
            Producto productoGuardado = productoRepository.save(p);
            p.setIdProducto(productoGuardado.getIdProducto());
            diferenciaStock = p.getStockActual();
        } else {
            // Buscamos el registro anterior para calcular la diferencia exacta de stock
            Optional<Producto> prodAnteriorOpt = productoRepository.findById(p.getIdProducto());
            int stockAnterior = prodAnteriorOpt.map(Producto::getStockActual).orElse(0);
            
            productoRepository.save(p);
            diferenciaStock = p.getStockActual() - stockAnterior;
        }

        // Si el stock cambió, gestionamos las series de Producto / medicamentos en consecuencia
        if (diferenciaStock > 0) {
            generarSeriesProducto(p.getIdProducto(), p.getCodigoSKU(), diferenciaStock);
        } else if (diferenciaStock < 0) {
            removerSeriesProducto(p.getIdProducto(), Math.abs(diferenciaStock));
        }
        
        return true;
    }

    /**
     * Optimización de rendimiento: Crea una lista de series en memoria 
     * y ejecuta una sola inserción masiva (Batch Insert) usando saveAll().
     */
    private void generarSeriesProducto(int idProducto, String sku, int cantidad) {
        List<Series> listaSeries = new ArrayList<>();
        
        // 1. Averiguamos cuántas cajas de este producto ya existen históricamente en la base de datos
        long cantidadExistente = seriesRepository.countByIdProducto(idProducto);

        // 2. Creamos un "Número de Lote Fijo" (Ej: 100000 + 22 = 100022). 
        // Así SIEMPRE será el mismo número grande para este producto en específico.
        long loteFijo = 100000 + idProducto;

        for (int i = 0; i < cantidad; i++) {
            Series serie = new Series();
            serie.setIdProducto(idProducto);
            
            // 3. El correlativo continúa desde donde se quedó.
            // Si ya tenías 13 registradas, el siguiente será el 14, 15, 16... ¡Jamás volverá a haber un -0 repetido!
            long correlativo = cantidadExistente + i + 1;
            
            // Formato final: SKU - LOTE_FIJO - CORRELATIVO
            String snUnico = sku + "-" + loteFijo + "-" + correlativo;
            
            serie.setNumeroSerie(snUnico);
            serie.setEstado("DISPONIBLE");             
            listaSeries.add(serie);
        }
        
        seriesRepository.saveAll(listaSeries);
    }

    /**
     * Utiliza la consulta nativa optimizada que reparamos en el repositorio
     */
    private void removerSeriesProducto(int idProducto, int cantidad) {
        // Ejecutamos el delete transaccional por lotes usando el método del SeriesRepository
        for (int i = 0; i < cantidad; i++) {
            seriesRepository.eliminarSeriesExcedentes(idProducto);
        }
    }
    
    @Transactional
    public boolean eliminarProducto(int id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Producto buscarProducto(int id) {
        return productoRepository.findById(id).orElse(null);
    }
}
