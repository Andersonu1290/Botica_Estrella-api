package com.boticaestrella.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.boticaestrella.modelo.DetalleVentaCliente;

@Repository
public interface DetalleVentaClienteRepository extends JpaRepository<DetalleVentaCliente, Integer> {

    // Obtener todos los detalles de un pedido
    List<DetalleVentaCliente> findByIdVentaCliente(int idVentaCliente);

    // Calcula cuántas unidades ha comprado el cliente (Excluyendo pedidos cancelados)
    // Calcula cuántas unidades ha comprado el cliente de forma segura
    @Query(value = "SELECT COALESCE(SUM(d.cantidad), 0) FROM detalle_venta_cliente d " +
                   "INNER JOIN venta_cliente v ON d.id_venta_cliente = v.id_venta_cliente " +
                   "WHERE v.id_usuario = :idUsuario AND d.id_producto = :idProducto AND v.estado NOT IN ('CANCELADO', 'ANULADA')", 
           nativeQuery = true)
    Object sumCantidadByUsuarioAndProducto(@Param("idUsuario") int idUsuario, 
                                           @Param("idProducto") int idProducto);

    // Lista todos los productos comprados de forma segura
    @Query(value = "SELECT DISTINCT d.id_producto FROM detalle_venta_cliente d " +
                   "INNER JOIN venta_cliente v ON d.id_venta_cliente = v.id_venta_cliente " +
                   "WHERE v.id_usuario = :idUsuario AND v.estado NOT IN ('CANCELADO', 'ANULADA')", 
           nativeQuery = true)
    List<Object> findProductosCompradosByUsuario(@Param("idUsuario") int idUsuario);
}

