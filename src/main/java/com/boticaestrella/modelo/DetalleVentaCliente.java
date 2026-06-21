package com.boticaestrella.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "detalle_venta_cliente")
public class DetalleVentaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private int idDetalle;

    @Column(name = "id_venta_cliente", nullable = false)
    private int idVentaCliente; // FK a venta_cliente

    @Column(name = "id_producto", nullable = false)
    private int idProducto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private double precioUnitario;

    @Column(name = "subtotal", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private double subtotal; // cantidad * precioUnitario

    @Column(name = "descuento", columnDefinition = "DECIMAL(10,2)")
    private double descuento;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Campo volátil para devolver info del producto al frontend
    @Transient
    private Producto producto;

    // ============== CONSTRUCTORES ==============
    public DetalleVentaCliente() {
    }

    public DetalleVentaCliente(int idVentaCliente, int idProducto, int cantidad,
                                double precioUnitario, double subtotal) {
        this.idVentaCliente = idVentaCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // ============== GETTERS Y SETTERS ==============
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdVentaCliente() { return idVentaCliente; }
    public void setIdVentaCliente(int idVentaCliente) { this.idVentaCliente = idVentaCliente; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}

