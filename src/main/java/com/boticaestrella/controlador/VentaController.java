package com.boticaestrella.controlador;

import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioVenta;
import com.boticaestrella.dto.VentaRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final ServicioVenta servicioVenta;

    public VentaController(ServicioVenta servicioVenta) {
        this.servicioVenta = servicioVenta;
    }

    /**
     * HISTORIAL DE VENTAS
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Venta>> obtenerHistorial() {
        return ResponseEntity.ok(servicioVenta.obtenerHistorialVentas());
    }

    /**
     * PROCESAR VENTA
     */
    @PostMapping
    public ResponseEntity<?> procesarVenta(@RequestBody VentaRequestDTO request) {
        try {
            // 🔥 AHORA RECIBE EL MAP (JSON) EXACTO DESDE EL SERVICIO
            Map<String, Object> resultado = servicioVenta.procesarSalidaProducto(
                    request.idProducto(),
                    request.nroSerie(),
                    request.tipoComprobante(), // Pasamos si es boleta o factura
                    request.idUsuario(),
                    request.docCliente(),
                    request.nombreCliente(),
                    request.correoCliente(),
                    request.metodoPago(),
                    request.total()
            );

            // 🔥 DEVUELVE EL MAP CON TODOS LOS MENSAJES DE TUS PATRONES
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ANULAR VENTA
     */
    @PostMapping("/anular/{idVenta}")
    public ResponseEntity<?> anularVenta(
            @PathVariable int idVenta,
            @RequestParam int idUsuario
    ) {
        try {
            servicioVenta.anularVenta(idVenta, idUsuario);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Venta anulada y stock reintegrado correctamente.")
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}