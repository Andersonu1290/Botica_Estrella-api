package com.boticaestrella.controlador;

import com.boticaestrella.dto.OcrResponseDTO;
import com.boticaestrella.modelo.TratamientoSmartCare;
import com.boticaestrella.servicio.SmartCareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/smartcare")
@CrossOrigin(origins = "*")
public class SmartCareController {

    private final SmartCareService smartCareService;

    public SmartCareController(SmartCareService smartCareService) {
        this.smartCareService = smartCareService;
    }

    // Endpoint 1: Recibe imagen y retorna JSON (OCR)
    @PostMapping(value = "/procesar-ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> procesarRecetaOcr(@RequestPart("imagen") MultipartFile imagen) {
        try {
            OcrResponseDTO resultadoOcr = smartCareService.procesarOcr(imagen);
            return ResponseEntity.ok(resultadoOcr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint 2: Guarda los datos una vez editados y validados
    @PostMapping("/tratamientos")
    public ResponseEntity<?> guardarTratamientoValidado(@RequestBody TratamientoSmartCare tratamiento) {
        try {
            TratamientoSmartCare guardado = smartCareService.registrarTratamiento(tratamiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint 3: Lista los tratamientos y dispara el flag de re-compra
    @GetMapping("/tratamientos/usuario/{id}")
    public ResponseEntity<List<TratamientoSmartCare>> listarMisTratamientos(@PathVariable int id) {
        List<TratamientoSmartCare> lista = smartCareService.listarHistorialPaciente(id);
        return ResponseEntity.ok(lista);
    }

    // =====================================================================
    // FASE 2: GESTIÓN DE CICLO DE VIDA (CRUD) Y CONSUMO
    // =====================================================================

    // Eliminar un tratamiento (menú de 3 puntos)
    @DeleteMapping("/tratamientos/{idTratamiento}")
    public ResponseEntity<?> eliminarTratamiento(@PathVariable int idTratamiento, @RequestParam int idUsuario) {
        try {
            smartCareService.eliminarTratamiento(idTratamiento, idUsuario);
            return ResponseEntity.ok(Map.of("mensaje", "Tratamiento eliminado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Registrar que el paciente tomó su medicamento
    @PutMapping("/tratamientos/{idTratamiento}/tomar-dosis")
    public ResponseEntity<?> registrarTomaDosis(@PathVariable int idTratamiento, @RequestParam int idUsuario) {
        try {
            TratamientoSmartCare actualizado = smartCareService.registrarToma(idTratamiento, idUsuario);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Actualizar (editar) la pauta médica de un tratamiento
    @PutMapping("/tratamientos/{idTratamiento}")
    public ResponseEntity<?> actualizarTratamiento(@PathVariable int idTratamiento, @RequestBody TratamientoSmartCare datosNuevos, @RequestParam int idUsuario) {
        try {
            TratamientoSmartCare actualizado = smartCareService.actualizarTratamiento(idTratamiento, datosNuevos, idUsuario);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // =====================================================================
    // BOTIQUÍN VIRTUAL (usado por NuevoTratamiento.vue)
    // =====================================================================

    // Consultar el stock real del botiquín virtual del paciente
    @GetMapping("/botiquin/stock")
    public ResponseEntity<Integer> consultarStockBotiquin(@RequestParam int idUsuario, @RequestParam int idProducto) {
        try {
            int stock = smartCareService.calcularStockRealComprado(idUsuario, idProducto);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            // Si hay un error, devolvemos 0 por seguridad para no bloquear el frontend
            return ResponseEntity.ok(0);
        }
    }
}