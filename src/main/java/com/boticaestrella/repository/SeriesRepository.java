package com.boticaestrella.repository;

import com.boticaestrella.modelo.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // ¡No olvides importar Optional!

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {
    
    // Consulta simple: buscar por producto y estado
    List<Series> findByIdProductoAndEstado(int idProducto, String estado);

    /**
     * 🔥 ESTO ES LO QUE FALTA:
     * Busca una serie específica por su número y estado actual.
     * Spring Data JPA generará el SQL automáticamente basándose en el nombre.
     */
    Optional<Series> findByNumeroSerieAndEstado(String numeroSerie, String estado);

    // Tu consulta de eliminación corregida para VS Code
    @Modifying
    @Query(value = "DELETE FROM series WHERE id_producto = :idProducto AND estado = 'DISPONIBLE' " +
                   "AND id_serie IN (SELECT id FROM (SELECT id_serie AS id FROM series WHERE id_producto = :idProducto AND estado = 'DISPONIBLE' ORDER BY id_serie DESC) as temp)", nativeQuery = true)
    void eliminarSeriesExcedentes(@Param("idProducto") int idProducto);

    // Cuenta las series filtrando por su estado exacto
    long countByEstado(String estado);
}
