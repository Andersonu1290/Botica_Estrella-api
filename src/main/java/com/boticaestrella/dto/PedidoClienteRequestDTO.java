package com.boticaestrella.dto;

import java.util.List;

public record PedidoClienteRequestDTO(
    // ============== DATOS DEL USUARIO ==============
    int idUsuario,
    String dniCliente,
    String nombreCliente,
    String apellidoCliente,
    String emailCliente,
    String telefonoCliente,

    // ============== DIRECCIÓN DE ENVÍO ==============
    String direccionEnvio,
    String numeroCalle,
    String apartamento,
    String ciudad,
    String departamento,
    String codigoPostal,

    // ============== TIPO DE ENVÍO ==============
    String tipoEnvio, // "NORMAL", "EXPRESS", "SAME_DAY"
    double costoEnvio,

    // ============== DATOS DE PAGO ==============
    String tipoPago, // "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA"
    String tipoTarjeta, // "VISA", "MASTERCARD"
    String ultimos4Digitos,
    String bancoTarjeta,
    String nombreTitular,

    // ============== ITEMS DEL CARRITO ==============
    List<ItemCarritoDTO> items, // Lista de productos con cantidad

    // ============== OBSERVACIONES ==============
    String observaciones
) {}

