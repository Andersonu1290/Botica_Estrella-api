package com.boticaestrella.repository;

import com.boticaestrella.modelo.TratamientoSmartCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TratamientoSmartCareRepository extends JpaRepository<TratamientoSmartCare, Integer> {
    
    // Devuelve todos los tratamientos del paciente, sin importar su estado
    List<TratamientoSmartCare> findByIdUsuario(int idUsuario);
}