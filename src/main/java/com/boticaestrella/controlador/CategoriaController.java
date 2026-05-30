package com.boticaestrella.controlador;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.modelo.Categoria;
import com.boticaestrella.repository.CategoriaRepository;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // GET /api/v1/categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    // POST /api/v1/categorias
    @PostMapping
    public ResponseEntity<?> guardarCategoria(@RequestBody Categoria categoria) {
        try {
            return new ResponseEntity<>(categoriaRepository.save(categoria), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar: " + e.getMessage());
        }
    }

    // DELETE /api/v1/categorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable int id) {
        try {
            if (categoriaRepository.existsById(id)) {
                categoriaRepository.deleteById(id);
                return ResponseEntity.ok("Eliminado");
            }
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            // 🔥 Esto atrapa el error de la base de datos y da un mensaje claro
            return ResponseEntity.status(HttpStatus.CONFLICT)
                   .body("No se puede eliminar: Esta categoría tiene productos asociados. Primero reasigna o elimina los productos.");
        }
    }
}