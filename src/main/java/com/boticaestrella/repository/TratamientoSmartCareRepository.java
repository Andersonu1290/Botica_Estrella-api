package com.boticaestrella.repository;

import com.boticaestrella.modelo.TratamientoSmartCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TratamientoSmartCareRepository extends JpaRepository<TratamientoSmartCare, Integer> {
    
    // El que ya tenías: Devuelve todos los tratamientos del paciente
    List<TratamientoSmartCare> findByIdUsuario(int idUsuario);

    // NUEVO: Busca solo los tratamientos ACTIVOS que consumen ese mismo medicamento
    @Query("SELECT t FROM TratamientoSmartCare t WHERE t.idUsuario = :idUsuario AND t.idProducto = :idProducto AND t.estado = 'ACTIVO'")
    List<TratamientoSmartCare> findActivosByUsuarioAndProducto(
            @Param("idUsuario") int idUsuario, 
            @Param("idProducto") int idProducto
    );
}