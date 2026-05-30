package com.boticaestrella.controlador;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boticaestrella.modelo.MovimientoKardex;
import com.boticaestrella.repository.KardexRepository;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin(origins = "*")
public class KardexController {

    private final KardexRepository kardexRepository;

    public KardexController(KardexRepository kardexRepository) {
        this.kardexRepository = kardexRepository;
    }

    @GetMapping("/historial")
    public ResponseEntity<List<MovimientoKardex>> obtenerHistorialKardex() {

        List<Object[]> resultados = kardexRepository.listarHistorialKardex();

        List<MovimientoKardex> historial = resultados.stream().map(r -> {

            MovimientoKardex k = new MovimientoKardex();

            k.setIdMovimiento(((Number) r[0]).intValue());
            k.setTipoMovimiento((String) r[1]);
            k.setCantidad(((Number) r[2]).intValue());
            k.setFecha(Timestamp.valueOf((java.time.LocalDateTime) r[3]));
            k.setMotivo((String) r[4]);

            k.setNombreProducto((String) r[5]);
            k.setNombreUsuario((String) r[6]);

            return k;

        }).toList();

        return ResponseEntity.ok(historial);
    }
}