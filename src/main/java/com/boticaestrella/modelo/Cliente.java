package com.boticaestrella.modelo;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int idCliente;

    @Column(name = "documento_identidad", length = 20, nullable = false)
    private String documentoIdentidad;

    @Column(name = "nombre_completo", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "correo", length = 100)
    private String correo;

    // Constructor vacío obligatorio para JPA
    public Cliente() {}

    // Constructor personalizado que ya tenías
    public Cliente(String documentoIdentidad, String nombreCompleto) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
    }

    // Getters y Setters
    public int getIdCliente() { 
        return idCliente; 
    }
    
    public void setIdCliente(int idCliente) { 
        return; // JPA se encarga de asignar el ID autoincremental
    }

    public String getDocumentoIdentidad() { 
        return documentoIdentidad; 
    }
    
    public void setDocumentoIdentidad(String documentoIdentidad) { 
        this.documentoIdentidad = documentoIdentidad; 
    }

    public String getNombreCompleto() { 
        return nombreCompleto; 
    }
    
    public void setNombreCompleto(String nombreCompleto) { 
        this.nombreCompleto = nombreCompleto; 
    }

    public String getCorreo() { 
        return correo; 
    }
    
    public void setCorreo(String correo) { 
        this.correo = correo; 
    }
}
