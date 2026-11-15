package com.boticaestrella.dto;

public record OcrResponseDTO(
    String nombreMedicamento,
    String dosis,
    String unidadMedida,
    int cantidadPorToma,
    int frecuenciaHoras,
    int cantidad,
    String textoCrudo,
    boolean requiereValidacionUsuario
) {}