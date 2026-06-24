package com.boticaestrella.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.boticaestrella.modelo.VentaCliente;

@Repository
public interface VentaClienteRepository extends JpaRepository<VentaCliente, Integer> {

    // Obtener todas las ventas de un cliente
    List<VentaCliente> findByIdUsuario(int idUsuario);

    // Obtener venta por número de pedido
    Optional<VentaCliente> findByNroPedido(String nroPedido);

    // Obtener ventas por estado
    List<VentaCliente> findByEstado(String estado);

    // Obtener ventas de un cliente por estado
    @Query("SELECT v FROM VentaCliente v WHERE v.idUsuario = :idUsuario AND v.estado = :estado")
    List<VentaCliente> findByIdUsuarioAndEstado(@Param("idUsuario") int idUsuario, @Param("estado") String estado);

    // Contar pedidos para generar número de pedido
    @Query("SELECT COUNT(v) FROM VentaCliente v")
    long countTotalVentas();

    // Obtener ventas pendientes de envío
    @Query("SELECT v FROM VentaCliente v WHERE v.estado IN ('PAGADO', 'PROCESANDO')")
    List<VentaCliente> findPendingShipments();

    // =========================================================================
    // 📊 CONSULTAS PARA EL DASHBOARD (E-COMMERCE)
    // =========================================================================

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM venta_cliente WHERE estado != 'CANCELADO'", nativeQuery = true)
    double obtenerTotalIngresosWeb();

    @Query(value = "SELECT COUNT(*) FROM venta_cliente WHERE estado != 'CANCELADO'", nativeQuery = true)
    int contarVentasCompletadasWeb();

    @Query(value = "SELECT p.nombre AS producto, SUM(d.cantidad) AS cantidad, c.nombre AS categoria " +
                   "FROM detalle_venta_cliente d " +
                   "INNER JOIN venta_cliente v ON d.id_venta_cliente = v.id_venta_cliente " +
                   "INNER JOIN productos p ON d.id_producto = p.id_producto " +
                   "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                   "WHERE v.estado != 'CANCELADO' " +
                   "GROUP BY d.id_producto, p.nombre, c.nombre", nativeQuery = true)
    List<Object[]> obtenerTopProductosWeb();
}

