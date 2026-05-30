package com.boticaestrella.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boticaestrella.modelo.Series;

@Repository
public interface MermaRepository extends JpaRepository<Series, Integer> {

    List<Series> findByEstado(String estado);

    Optional<Series> findByNumeroSerieAndEstado(String numeroSerie, String estado);

    @Query(value = """
        SELECT 
            s.id_serie AS idSerie,
            s.numero_serie AS numeroSerie,
            s.id_producto AS idProducto,
            s.estado AS estado,
            p.nombre AS nombreProducto,
            p.codigo_SKU AS codigoSKU
        FROM series s
        INNER JOIN productos p
            ON s.id_producto = p.id_producto
        WHERE s.estado = :estado
        """, nativeQuery = true)
    List<Object[]> listarSeriesConProducto(@Param("estado") String estado);
}