package com.boticaestrella.dto;

public record VentaRequestDTO(
    int idProducto,
    String nroSerie,
    String comprobante,
    String tipoComprobante,
    String metodoPago,
    int idUsuario,
    String docCliente,
    String nombreCliente,
    String correoCliente,
    double total
) {}