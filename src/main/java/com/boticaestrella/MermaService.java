package com.boticaestrella.servicio;

import com.boticaestrella.modelo.Series;
import com.boticaestrella.modelo.Producto;
import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.repository.MermaRepository;
import com.boticaestrella.repository.ProductoRepository;
import com.boticaestrella.repository.KardexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class MermaService {

    private final MermaRepository mermaRepository;
    private final ProductoRepository productoRepository;
    private final KardexRepository kardexRepository;

    // Inyección de dependencias por constructor
    public MermaService(MermaRepository mermaRepository, 
                        ProductoRepository productoRepository, 
                        KardexRepository kardexRepository) {
        this.mermaRepository = mermaRepository;
        this.productoRepository = productoRepository;
        this.kardexRepository = kardexRepository;
    }

    /**
     * Procesa la baja de un medicamento o producto por merma de forma transaccional.
     * Reemplaza por completo el bloque complejo de consultas que tenías en MermaDAO.
     */
    @Transactional(rollbackFor = Exception.class)
    public void procesarMerma(String nroSerie, String motivo, int idUsuario) throws Exception {
        
        // 1. Validar y obtener la serie en estado disponible
        Series serie = mermaRepository.findByNumeroSerieAndEstado(nroSerie, "DISPONIBLE")
                .orElseThrow(() -> new Exception("La serie '" + nroSerie + "' no está disponible o no existe en el inventario."));

        // 2. Cambiar estado de la serie a 'MERMA' y actualizar base de datos
        serie.setEstado("MERMA");
        mermaRepository.save(serie);

        // 3. Descontar el stock actual del producto asociado
        Producto producto = productoRepository.findById(serie.getIdProducto())
                .orElseThrow(() -> new Exception("El producto asociado a la serie ya no existe en el catálogo."));
        
        producto.setStockActual(producto.getStockActual() - 1);
        productoRepository.save(producto);

        // 4. Registrar la salida física por descarte en el historial del Kardex
        MovimientoKardex kardex = new MovimientoKardex();
        kardex.setIdProducto(producto.getIdProducto());
        kardex.setTipoMovimiento("MERMA");
        kardex.setCantidad(1);
        kardex.setMotivo("MERMA S/N: " + nroSerie + " | Motivo: " + motivo);
        kardex.setIdUsuario(idUsuario);
        kardex.setFecha(new Timestamp(System.currentTimeMillis()));
        
        kardexRepository.save(kardex);
    }
}
