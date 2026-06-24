package com.boticaestrella.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.dto.VentaRequestDTO;
import com.boticaestrella.modelo.Venta;
import com.boticaestrella.servicio.ServicioVenta;
import com.boticaestrella.modelo.Cliente;

@RestController
@RequestMapping("/api/v1/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final ServicioVenta servicioVenta;

    public VentaController(ServicioVenta servicioVenta) {
        this.servicioVenta = servicioVenta;
    }

    /**
     * HISTORIAL DE VENTAS (Mantenido para el Administrador)
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Venta>> obtenerHistorial() {
        return ResponseEntity.ok(servicioVenta.obtenerHistorialVentas());
    }

    /**
     * 🔥 NUEVO: HISTORIAL EXCLUSIVO DEL CLIENTE LOGUEADO (Para el perfil Vue)
     */
    @GetMapping("/mis-compras/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> obtenerMisCompras(@PathVariable int idUsuario) {
        return ResponseEntity.ok(servicioVenta.obtenerHistorialCliente(idUsuario));
    }

    /**
     * AUTOCOMPLETADO DE CLIENTES POR DNI/RUC
     */
    @GetMapping("/cliente/{dni}")
    public ResponseEntity<?> buscarCliente(@PathVariable String dni) {
        com.boticaestrella.modelo.Cliente cliente = servicioVenta.buscarClientePorDni(dni);
        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Cliente nuevo"));
    }

    /**
     * PROCESAR VENTA
     */
    @PostMapping
    public ResponseEntity<?> procesarVenta(@RequestBody VentaRequestDTO request) {
        // ... [Se queda exactamente como lo tenías]
        try {
            Map<String, Object> resultado = servicioVenta.procesarSalidaProducto(
                    request.idProducto(),
                    request.nroSerie(),
                    request.tipoComprobante(),
                    request.idUsuario(),
                    request.docCliente(),
                    request.nombreCliente(),
                    request.correoCliente(),
                    request.metodoPago(),
                    request.total()
            );
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
        // ... [Se queda exactamente como lo tenías]
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