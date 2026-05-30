package com.boticaestrella.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boticaestrella.modelo.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    /**
     * Reemplaza tu antiguo método "listarVentas()".
     * Realiza un INNER JOIN nativo para enlazar las tablas y rellenar los campos @Transient 
     * (nombreCompleto para el cliente y producto_nombre para el medicamento).
     */
    @Query(value = "SELECT v.id_venta, v.id_cliente, v.id_usuario, v.id_producto, v.nro_serie, " +
                   "v.nro_comprobante, v.metodo_pago, v.total, v.fecha_venta, v.estado, " +
                   "c.nombre_completo AS nombre_cliente, p.nombre AS nombre_producto " +
                   "FROM ventas v " +
                   "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                   "INNER JOIN productos p ON v.id_producto = p.id_producto " +
                   "ORDER BY v.fecha_venta DESC", nativeQuery = true)
    List<Object[]> listarVentasConNombres(); 

    /* =========================================================================
       📊 SECCIÓN DE CONSULTAS PARA REPORTES 
       ========================================================================= */

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE estado = 'COMPLETADA'", nativeQuery = true)
    double obtenerTotalIngresos();

    @Query(value = "SELECT COUNT(*) FROM ventas WHERE metodo_pago = :metodo AND estado = 'COMPLETADA'", nativeQuery = true)
    long contarVentasPorMetodoPago(@Param("metodo") String metodo);

    /**
     * Reporte: Top productos más vendidos incluyendo su categoría.
     */
    @Query(value = "SELECT p.nombre AS producto, COUNT(v.id_producto) AS cantidad, c.nombre AS categoria " +
                   "FROM ventas v " +
                   "INNER JOIN productos p ON v.id_producto = p.id_producto " +
                   "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                   "WHERE v.estado = 'COMPLETADA' " +
                   "GROUP BY v.id_producto, p.nombre, c.nombre " +
                   "ORDER BY cantidad DESC", nativeQuery = true)
    List<Object[]> obtenerTopProductosConCategoria(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM ventas WHERE estado = 'COMPLETADA'", nativeQuery = true)
    int contarVentasCompletadas();

    /* =========================================================================
       🛒 SECCIÓN E-COMMERCE (NUEVO)
       ========================================================================= */

    /**
     * Historial exclusivo para el cliente logueado en la tienda web.
     */
    @Query(value = "SELECT v.nro_comprobante, v.fecha_venta, v.metodo_pago, v.total, v.estado, p.nombre AS nombre_producto " +
                   "FROM ventas v " +
                   "INNER JOIN productos p ON v.id_producto = p.id_producto " +
                   "WHERE v.id_usuario = :idUsuario " +
                   "ORDER BY v.fecha_venta DESC", nativeQuery = true)
    List<Object[]> listarMisComprasRealizadas(@Param("idUsuario") int idUsuario);
}