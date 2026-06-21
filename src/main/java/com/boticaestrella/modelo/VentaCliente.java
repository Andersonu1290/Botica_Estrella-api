package com.boticaestrella.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "venta_cliente")
public class VentaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta_cliente")
    private int idVentaCliente;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @Column(name = "nro_pedido", unique = true, nullable = false, length = 20)
    private String nroPedido; // Ej: "PED-2025-00001"

    @Column(name = "estado", nullable = false, length = 30)
    private String estado; // "PENDIENTE", "PAGADO", "ENVIADO", "ENTREGADO", "CANCELADO"

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    // ============== TOTALES ==============
    @Column(name = "subtotal", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private double subtotal;

    @Column(name = "costo_envio", columnDefinition = "DECIMAL(10,2)")
    private double costoEnvio;

    @Column(name = "total", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private double total;

    // ============== DATOS DEL CLIENTE ==============
    @Column(name = "dni_cliente", length = 20)
    private String dniCliente;

    @Column(name = "nombre_cliente", nullable = false, length = 100)
    private String nombreCliente;

    @Column(name = "apellido_cliente", length = 100)
    private String apellidoCliente;

    @Column(name = "email_cliente", nullable = false, length = 100)
    private String emailCliente;

    @Column(name = "telefono_cliente", length = 15)
    private String telefonoCliente;

    // ============== DIRECCIÓN DE ENVÍO ==============
    @Column(name = "direccion_envio", nullable = false, length = 255)
    private String direccionEnvio;

    @Column(name = "numero_calle", length = 10)
    private String numeroCalle;

    @Column(name = "apartamento", length = 50)
    private String apartamento;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "departamento", nullable = false, length = 100)
    private String departamento;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    // ============== TIPO DE ENVÍO ==============
    @Column(name = "tipo_envio", nullable = false, length = 50)
    private String tipoEnvio; // "NORMAL", "EXPRESS", "SAME_DAY"

    @Column(name = "numero_seguimiento", length = 100)
    private String numeroSeguimiento;

    // ============== DATOS DE PAGO ==============
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private String tipoPago; // "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA", "YAPE", "PLIN"

    @Column(name = "tipo_tarjeta", length = 30)
    private String tipoTarjeta; // "VISA", "MASTERCARD", "AMERICAN_EXPRESS"

    @Column(name = "ultimos_4_digitos", length = 4)
    private String ultimos4Digitos;

    @Column(name = "banco_tarjeta", length = 100)
    private String bancoTarjeta;

    @Column(name = "nombre_titular", length = 100)
    private String nombreTitular;

    // ============== OBSERVACIONES Y AUDITORÍA ==============
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    // ============== CONSTRUCTORES ==============
    public VentaCliente() {
    }

    public VentaCliente(int idUsuario, String nroPedido, String estado, LocalDateTime fechaPedido,
                        double subtotal, double costoEnvio, double total, String dniCliente,
                        String nombreCliente, String emailCliente, String telefonoCliente,
                        String direccionEnvio, String ciudad, String departamento, String tipoEnvio,
                        String tipoPago) {
        this.idUsuario = idUsuario;
        this.nroPedido = nroPedido;
        this.estado = estado;
        this.fechaPedido = fechaPedido;
        this.subtotal = subtotal;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.dniCliente = dniCliente;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.telefonoCliente = telefonoCliente;
        this.direccionEnvio = direccionEnvio;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.tipoEnvio = tipoEnvio;
        this.tipoPago = tipoPago;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ============== GETTERS Y SETTERS ==============
    public int getIdVentaCliente() { return idVentaCliente; }
    public void setIdVentaCliente(int idVentaCliente) { this.idVentaCliente = idVentaCliente; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNroPedido() { return nroPedido; }
    public void setNroPedido(String nroPedido) { this.nroPedido = nroPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; this.fechaActualizacion = LocalDateTime.now(); }

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

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getNumeroCalle() { return numeroCalle; }
    public void setNumeroCalle(String numeroCalle) { this.numeroCalle = numeroCalle; }

    public String getApartamento() { return apartamento; }
    public void setApartamento(String apartamento) { this.apartamento = apartamento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

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

    public String getBancoTarjeta() { return bancoTarjeta; }
    public void setBancoTarjeta(String bancoTarjeta) { this.bancoTarjeta = bancoTarjeta; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public LocalDateTime getFechaEntregaReal() { return fechaEntregaReal; }
    public void setFechaEntregaReal(LocalDateTime fechaEntregaReal) { this.fechaEntregaReal = fechaEntregaReal; }
}

