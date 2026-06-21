package com.boticaestrella.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoClienteResponseDTO {
    private int idVentaCliente;
    private String nroPedido;
    private String estado;
    private LocalDateTime fechaPedido;
    private LocalDate fechaEntregaEstimada;
    private double subtotal;
    private double costoEnvio;
    private double total;
    private String nombreCliente;
    private String emailCliente;
    private String telefonoCliente;
    private String direccionEnvio;
    private String ciudad;
    private String departamento;
    private String tipoEnvio;
    private String numeroSeguimiento;
    private String tipoPago;
    private String tipoTarjeta;
    private String ultimos4Digitos;
    private List<DetalleVentaClienteDTO> detalles;

    // Constructores
    public PedidoClienteResponseDTO() {
    }

    public PedidoClienteResponseDTO(int idVentaCliente, String nroPedido, String estado,
                                     LocalDateTime fechaPedido, LocalDate fechaEntregaEstimada,
                                     double subtotal, double costoEnvio, double total,
                                     String nombreCliente, String emailCliente, String telefonoCliente,
                                     String direccionEnvio, String ciudad, String departamento,
                                     String tipoEnvio, String numeroSeguimiento, String tipoPago,
                                     String tipoTarjeta, String ultimos4Digitos) {
        this.idVentaCliente = idVentaCliente;
        this.nroPedido = nroPedido;
        this.estado = estado;
        this.fechaPedido = fechaPedido;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.subtotal = subtotal;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.telefonoCliente = telefonoCliente;
        this.direccionEnvio = direccionEnvio;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.tipoEnvio = tipoEnvio;
        this.numeroSeguimiento = numeroSeguimiento;
        this.tipoPago = tipoPago;
        this.tipoTarjeta = tipoTarjeta;
        this.ultimos4Digitos = ultimos4Digitos;
    }

    // Getters y Setters
    public int getIdVentaCliente() { return idVentaCliente; }
    public void setIdVentaCliente(int idVentaCliente) { this.idVentaCliente = idVentaCliente; }

    public String getNroPedido() { return nroPedido; }
    public void setNroPedido(String nroPedido) { this.nroPedido = nroPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDate getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(double costoEnvio) { this.costoEnvio = costoEnvio; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }

    public String getNumeroSeguimiento() { return numeroSeguimiento; }
    public void setNumeroSeguimiento(String numeroSeguimiento) { this.numeroSeguimiento = numeroSeguimiento; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public String getTipoTarjeta() { return tipoTarjeta; }
    public void setTipoTarjeta(String tipoTarjeta) { this.tipoTarjeta = tipoTarjeta; }

    public String getUltimos4Digitos() { return ultimos4Digitos; }
    public void setUltimos4Digitos(String ultimos4Digitos) { this.ultimos4Digitos = ultimos4Digitos; }

    public List<DetalleVentaClienteDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaClienteDTO> detalles) { this.detalles = detalles; }
}

