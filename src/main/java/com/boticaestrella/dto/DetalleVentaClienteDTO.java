package com.boticaestrella.dto;

public class DetalleVentaClienteDTO {
    private int idDetalle;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private double descuento;

    // Constructores
    public DetalleVentaClienteDTO() {
    }

    public DetalleVentaClienteDTO(int idDetalle, int idProducto, String nombreProducto,
                                   int cantidad, double precioUnitario, double subtotal, double descuento) {
        this.idDetalle = idDetalle;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.descuento = descuento;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
}

