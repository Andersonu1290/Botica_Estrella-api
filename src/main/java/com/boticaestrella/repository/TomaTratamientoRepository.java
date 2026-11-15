package com.boticaestrella.repository;

import com.boticaestrella.modelo.TomaTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TomaTratamientoRepository extends JpaRepository<TomaTratamiento, Integer> {
    
    // Obtiene todo el cronograma de un tratamiento específico, ordenado cronológicamente
    List<TomaTratamiento> findByIdTratamientoOrderByFechaHoraProgramadaAsc(int idTratamiento);
    
    // Cuenta cuántas tomas se han marcado como "TOMADO" para calcular el % de adherencia
    long countByIdTratamientoAndEstado(int idTratamiento, String estado);
    
    // Cuenta el total de tomas de un tratamiento
    long countByIdTratamiento(int idTratamiento);

    // Calcula cuántas unidades de ese producto ya se ha tomado históricamente
    @Query(value = "SELECT COALESCE(SUM(ts.cantidad_por_toma), 0) FROM tomas_tratamiento t " +
                   "INNER JOIN tratamientos_smartcare ts ON t.id_tratamiento = ts.id_tratamiento " +
                   "WHERE ts.id_usuario = :idUsuario AND ts.id_producto = :idProducto AND t.estado = :estado", 
           nativeQuery = true)
    Integer sumCantidadConsumidaByUsuarioAndProducto(@Param("idUsuario") int idUsuario, 
                                                     @Param("idProducto") int idProducto, 
                                                     @Param("estado") String estado);
}