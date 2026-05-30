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
    List<Object[]> listarVentasConNombres(); // Cambiado a Object[]

    /* =========================================================================
       📊 SECCIÓN DE CONSULTAS PARA REPORTES (Mencionadas anteriormente)
       ========================================================================= */

    /**
     * Reporte: Total de ingresos generados por ventas completadas.
     * Ideal para el Dashboard o panel de control principal de la botica.
     */
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE estado = 'COMPLETADA'", nativeQuery = true)
    double obtenerTotalIngresos();

    /**
     * Reporte: Conteo de ventas realizadas por un método de pago específico (Efectivo, Tarjeta, Yape/Plin).
     */
    @Query(value = "SELECT COUNT(*) FROM ventas WHERE metodo_pago = :metodo AND estado = 'COMPLETADA'", nativeQuery = true)
    long contarVentasPorMetodoPago(@Param("metodo") String metodo);

    /**
     * Reporte: Top productos más vendidos de la botica Estrella.
     * Devuelve una lista de arreglos de objetos, útil para mapear un gráfico de barras.
     */

    @Query(value = "SELECT p.nombre, COUNT(v.id_producto) AS cantidad " +
                   "FROM ventas v " +
                   "INNER JOIN productos p ON v.id_producto = p.id_producto " +
                   "WHERE v.estado = 'COMPLETADA' " +
                   "GROUP BY v.id_producto " +
                   "ORDER BY cantidad DESC", nativeQuery = true)
    List<Object[]> obtenerProductosMasVendidos(Pageable pageable);

    // Cuenta SOLO las ventas que no han sido anuladas
    @Query(value = "SELECT COUNT(*) FROM ventas WHERE estado = 'COMPLETADA'", nativeQuery = true)
    int contarVentasCompletadas();

}
