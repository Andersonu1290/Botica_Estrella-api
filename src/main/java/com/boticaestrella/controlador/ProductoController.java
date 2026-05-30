package com.boticaestrella.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boticaestrella.modelo.Producto;
import com.boticaestrella.servicio.ServicioProducto;
@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*") // Permite la conexión segura con cualquier frontend
public class ProductoController {

    private final ServicioProducto servicioProducto;

    // Inyección de dependencias por constructor
    public ProductoController(ServicioProducto servicioProducto) {
        this.servicioProducto = servicioProducto;
    }

    /**
     * Endpoint: GET /api/v1/productos
     * Reemplaza por completo el bloque "listar" de tu antiguo Servlet.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = servicioProducto.obtenerInventarioActivo();
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint: GET /api/v1/productos/{id}
     * Reemplaza por completo la lógica previa a la renderización del formulario "editar".
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Producto producto = servicioProducto.buscarProducto(id);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El producto con ID " + id + " no fue encontrado."));
    }

    /**
     * Endpoint: POST /api/v1/productos
     * Reemplaza por completo la lógica "guardar" del doPost para inserciones y actualizaciones.
     * Al usar consume MULTIPART_FORM_DATA_VALUE se permite recibir los textos de los campos y la imagen a la vez.
     */

    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable int id) {
    
        Producto producto = servicioProducto.buscarProducto(id);
    
        if (producto == null || producto.getImagen() == null) {
            return ResponseEntity.notFound().build();
        }
    
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(producto.getImagen());
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardarProducto(
            @RequestParam(value = "idProducto", defaultValue = "0") int idProducto,
            @RequestParam("codigoSKU") String codigoSKU,
            @RequestParam("nombre") String nombre,
            @RequestParam("idCategoria") int idCategoria,
            @RequestParam("stockActual") int stockActual,
            @RequestParam("stockMinimo") int stockMinimo,
            @RequestParam("precio") double precio,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile) {

        try {
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            producto.setCodigoSKU(codigoSKU);
            producto.setNombre(nombre);
            producto.setIdCategoria(idCategoria);
            producto.setStockActual(stockActual);
            producto.setStockMinimo(stockMinimo);
            producto.setPrecio(precio);

            // Si el cliente adjuntó una imagen, extraemos sus bytes de forma nativa con Spring
            if (imagenFile != null && !imagenFile.isEmpty()) {
                producto.setImagen(imagenFile.getBytes());
            } else if (idProducto != 0) {
                // Si estamos editando y no enviaron imagen nueva, recuperamos la imagen anterior para no borrarla
                Producto prodExistente = servicioProducto.buscarProducto(idProducto);
                if (prodExistente != null) {
                    producto.setImagen(prodExistente.getImagen());
                }
            }

            // Ejecutamos tu servicio transaccional que gestiona el inventario y las series en cascada
            servicioProducto.guardarProducto(producto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Producto procesado y guardado correctamente en la botica."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al guardar el producto: " + e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /api/v1/productos/{id}
     * Reemplaza por completo el bloque "eliminar" de tu antiguo Servlet.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id) {
        boolean eliminado = servicioProducto.eliminarProducto(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente de los registros."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se pudo eliminar. El producto no existe."));
    }

}
