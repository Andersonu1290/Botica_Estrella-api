package com.boticaestrella.repository;

import com.boticaestrella.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Equivale a tu antiguo buscarPorSKU
    Optional<Producto> findByCodigoSKU(String codigoSKU);
    
    /**
     * Reporte de Inventario: Listar productos con stock crítico.
     * Busca los productos donde el stock actual sea menor o igual al stock mínimo configurado.
     * Spring genera el SQL de forma automática a partir del nombre del método.
     */
    List<Producto> findByStockActualLessThanEqual(int stockMinimo);

    /* =========================================================================
       📊 SECCIÓN DE CONSULTAS PARA REPORTES (Provenientes de tu antiguo ReporteDAO)
       ========================================================================= */

    /**
     * Reporte: Valor total del inventario actual en la botica.
     * Multiplica el precio por el stock actual de todos los productos para saber cuánto dinero hay invertido.
     */
    @Query(value = "SELECT COALESCE(SUM(precio * stock_actual), 0) FROM productos", nativeQuery = true)
    double obtenerValorTotalInventario();

    /**
     * Reporte: Contar cuántos productos están completamente agotados (Stock en cero).
     */
    @Query(value = "SELECT COUNT(*) FROM productos WHERE stock_actual = 0", nativeQuery = true)
    long contarProductosAgotados();

    /**
     * Reporte Avanzado: Buscar productos por coincidencia de nombre o SKU.
     * Ideal para el buscador predictivo que usará tu frontend.
     */
    @Query(value = "SELECT * FROM productos WHERE nombre LIKE CONCAT('%', :termino, '%') OR codigo_SKU LIKE CONCAT('%', :termino, '%')", nativeQuery = true)
    List<Producto> buscarPorNombreOSku(@Param("termino") String termino);

    // 1. Suma el stock real (unidades físicas) de todos los productos
    @Query(value = "SELECT COALESCE(SUM(stock_actual), 0) FROM productos", nativeQuery = true)
    int obtenerTotalUnidadesStock();

    // 2. Cuenta productos donde el stock actual ya tocó o bajó del mínimo
    @Query(value = "SELECT COUNT(*) FROM productos WHERE stock_actual <= stock_minimo", nativeQuery = true)
    int contarStockCritico();

}
