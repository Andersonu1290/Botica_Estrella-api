package com.boticaestrella.controlador;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es una API REST (devuelve JSON)
@RequestMapping("/api/v1/categorias") // Ruta base para todos los endpoints
@CrossOrigin(origins = "*") // Permite que un frontend (React, Angular, etc.) se conecte sin bloqueos
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    // Inyectamos el repositorio que creamos en la Fase 3
    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Endpoint: GET /api/v1/categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> lista = categoriaRepository.findAll();
        return ResponseEntity.ok(lista); // Devuelve HTTP 200 OK
    }

    // Endpoint: POST /api/v1/categorias
    @PostMapping
    public ResponseEntity<Categoria> guardarCategoria(@RequestBody Categoria categoria) {
        // @RequestBody toma el JSON del cliente y lo convierte en el objeto Categoria
        Categoria nuevaCategoria = categoriaRepository.save(categoria);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED); // Devuelve HTTP 201 Created
    }

    // Endpoint: DELETE /api/v1/categorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable int id) {
        // @PathVariable extrae el número de la URL
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.notFound().build(); // HTTP 404 Not Found
    }
}