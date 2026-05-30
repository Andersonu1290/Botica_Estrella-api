package com.boticaestrella.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.modelo.Series;
import com.boticaestrella.repository.MermaRepository;
import com.boticaestrella.servicio.MermaService;

@RestController
@RequestMapping("/api/v1/mermas")
@CrossOrigin(origins = "*")
public class MermaController {

    private final MermaRepository mermaRepository;
    private final MermaService mermaService;

    // Inyección por constructor
    public MermaController(MermaRepository mermaRepository, MermaService mermaService) {
        this.mermaRepository = mermaRepository;
        this.mermaService = mermaService;
    }

    /**
     * GET:
     * /api/v1/mermas/series?estado=DISPONIBLE
     */
    @GetMapping("/series")
    public ResponseEntity<List<Series>> listarSeriesPorEstado(
            @RequestParam(value = "estado", defaultValue = "DISPONIBLE") String estado) {
            
        List<Object[]> resultados = mermaRepository.listarSeriesConProducto(estado);
            
        List<Series> listaSeries = resultados.stream().map(r -> {
        
            Series s = new Series();
        
            s.setIdSerie(((Number) r[0]).intValue());
            s.setNumeroSerie(String.valueOf(r[1]));
            s.setIdProducto(((Number) r[2]).intValue());
            s.setEstado(String.valueOf(r[3]));
        
            // AQUÍ ESTABA EL PROBLEMA
            s.setNombreProducto(r[4] != null ? String.valueOf(r[4]) : "-");
            s.setCodigoSKU(r[5] != null ? String.valueOf(r[5]) : "-");
        
            return s;
        
        }).toList();
    
        return ResponseEntity.ok(listaSeries);
    }

    /**
     * POST:
     * /api/v1/mermas/procesar
     */
    @PostMapping("/procesar")
    public ResponseEntity<?> registrarMerma(
            @RequestBody Map<String, Object> requestPayload) {

        try {

            String nroSerie = (String) requestPayload.get("numeroSerie");
            String motivo = (String) requestPayload.get("motivo");
            Integer idUsuario = (Integer) requestPayload.get("idUsuario");

            // Validación
            if (nroSerie == null || motivo == null || idUsuario == null) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "error",
                                "Faltan datos obligatorios: numeroSerie, motivo o idUsuario."
                        ));
            }

            // Procesar merma
            mermaService.procesarMerma(nroSerie, motivo, idUsuario);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "La serie " + nroSerie + " fue dada de baja exitosamente."
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error",
                            "No se pudo procesar la merma: " + e.getMessage()
                    ));
        }
    }
}