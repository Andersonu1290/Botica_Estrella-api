package com.boticaestrella.repository;

import com.boticaestrella.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Al heredar de JpaRepository, automáticamente hay:
    // save(categoria) -> Para insertar o actualizar
    // findAll() -> Para listar todas
    // deleteById(id) -> Para eliminar
    // findById(id) -> Para buscar una por ID
}