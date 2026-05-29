package com.boticaestrella.repository;

import com.boticaestrella.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca un cliente por su documento de identidad (DNI, RUC, etc.).
     * Reemplaza la consulta manual que tenías dentro de VentaDAO.registrarVenta.
     */
    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);
    
    /**
     * Verifica si existe un cliente registrado con ese documento.
     * Útil para validaciones rápidas antes de registrar una venta.
     */
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}