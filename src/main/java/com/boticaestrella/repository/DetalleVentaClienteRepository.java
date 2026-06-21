package com.boticaestrella.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.boticaestrella.modelo.DetalleVentaCliente;

@Repository
public interface DetalleVentaClienteRepository extends JpaRepository<DetalleVentaCliente, Integer> {

    // Obtener todos los detalles de un pedido
    List<DetalleVentaCliente> findByIdVentaCliente(int idVentaCliente);
}

