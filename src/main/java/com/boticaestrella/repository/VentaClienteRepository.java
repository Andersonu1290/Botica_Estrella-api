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
}

