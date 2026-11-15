package com.boticaestrella.repository;

import com.boticaestrella.modelo.TratamientoSmartCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TratamientoSmartCareRepository extends JpaRepository<TratamientoSmartCare, Integer> {
    
    // Lista los tratamientos de un paciente específico filtrados por su estado
    List<TratamientoSmartCare> findByIdUsuarioAndEstado(int idUsuario, String estado);
}