package com.boticaestrella.controlador;

import com.boticaestrella.modelo.CarritoItem;
import com.boticaestrella.servicio.ServicioCarrito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final ServicioCarrito servicioCarrito;

    public CarritoController(ServicioCarrito servicioCarrito) {
        this.servicioCarrito = servicioCarrito;
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<CarritoItem>> verCarrito(@PathVariable int idUsuario) {
        return ResponseEntity.ok(servicioCarrito.obtenerCarritoDeUsuario(idUsuario));
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarItem(@RequestBody Map<String, Integer> payload) {
        servicioCarrito.agregarOActualizarItem(
            payload.get("idUsuario"), 
            payload.get("idProducto"), 
            payload.get("cantidad")
        );
        return ResponseEntity.ok(Map.of("mensaje", "Producto guardado o actualizado en el carrito."));
    }

    @DeleteMapping("/item/{idItem}")
    public ResponseEntity<?> eliminarItem(@PathVariable int idItem) {
        servicioCarrito.eliminarItem(idItem);
        return ResponseEntity.ok(Map.of("mensaje", "Item eliminado del carrito."));
    }

    @DeleteMapping("/vaciar/{idUsuario}")
    public ResponseEntity<?> vaciarCarrito(@PathVariable int idUsuario) {
        servicioCarrito.vaciarCarrito(idUsuario);
        return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado exitosamente."));
    }
}