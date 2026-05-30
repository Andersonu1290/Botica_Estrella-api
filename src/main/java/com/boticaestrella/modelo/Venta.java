package com.boticaestrella.modelo;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ventas")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta") // Asumiendo que se llama así en tu BD
    private int id;

    @Column(name = "id_cliente")
    private int idCliente;

    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "id_producto")
    private int idProducto;

    @Column(name = "nro_serie")
    private String nroSerie;

    @Column(name = "nro_comprobante")
    private String nroComprobante;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "total", columnDefinition = "DECIMAL(10,2)") // Fuerza a Hibernate a validar como DECIMAL
    private double total;


    @Temporal(TemporalType.TIMESTAMP) // Manejo de Fecha y Hora en JPA
    @Column(name = "fecha_venta") // Asumiendo que se llama así en tu BD
    private Date fecha;

    @Column(name = "estado")
    private String estado;
    
    // Estos campos provienen de otras tablas en tus consultas SQL manuales.
    // Con @Transient evitas errores de mapeo.
    @Transient
    private String nombreCliente;
    
    @Transient
    private String nombreProducto;

    public double calcularTotal(double precio, int cantidad) {
        this.total = precio * cantidad;
        return this.total;
    }

    // ... (Mantén exactamente los mismos Getters y Setters que ya tenías) ...

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNroSerie() { return nroSerie; }
    public void setNroSerie(String nroSerie) { this.nroSerie = nroSerie; }
    public String getNroComprobante() { return nroComprobante; }
    public void setNroComprobante(String nroComprobante) { this.nroComprobante = nroComprobante; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}