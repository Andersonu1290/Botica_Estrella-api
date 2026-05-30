package com.boticaestrella.modelo;

import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int idProducto;

    @Column(name = "codigo_SKU")
    private String codigoSKU;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "id_categoria")
    private int idCategoria;

    @Column(name = "stock_actual")
    private int stockActual;

    @Column(name = "stock_minimo")
    private int stockMinimo;

    @Column(name = "precio", columnDefinition = "DECIMAL(10,2)")
    private double precio;

    @Lob
    @Column(name = "imagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Transient
    private String base64Imagen;

    public Producto() {}

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoSKU() {
        return codigoSKU;
    }

    public void setCodigoSKU(String codigoSKU) {
        this.codigoSKU = codigoSKU;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
        if (imagen != null && imagen.length > 0) {
            this.base64Imagen = Base64.getEncoder().encodeToString(imagen);
        }
    }

    public String getBase64Imagen() {
        return base64Imagen;
    }
}
