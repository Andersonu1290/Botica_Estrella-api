package com.boticaestrella.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.sql.Timestamp;

@Entity
@Table(name = "kardex_movimientos")
public class MovimientoKardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private int idMovimiento;

    @Column(name = "id_producto", nullable = false)
    private int idProducto;

    @Column(name = "tipo_movimiento", length = 50, nullable = false)
    private String tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "fecha", nullable = false)
    private Timestamp fecha;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    // Campos "extra" ignorados por JPA para reportes y respuestas de la API
    @Transient
    private String nombreProducto;

    @Transient
    private String nombreUsuario;    

    // Constructor vacío obligatorio para JPA
    public MovimientoKardex() {}

    // Getters y Setters
    public int getIdMovimiento() { 
        return idMovimiento; 
    }
    
    public void setIdMovimiento(int idMovimiento) { 
        this.idMovimiento = idMovimiento; 
    }

    public int getIdProducto() { 
        return idProducto; 
    }
    
    public void setIdProducto(int idProducto) { 
        this.idProducto = idProducto; 
    }

    public String getTipoMovimiento() { 
        return tipoMovimiento; 
    }
    
    public void setTipoMovimiento(String tipoMovimiento) { 
        this.tipoMovimiento = tipoMovimiento; 
    }

    public int getCantidad() { 
        return cantidad; 
    }
    
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad; 
    }

    public Timestamp getFecha() { 
        return fecha; 
    }
    
    public void setFecha(Timestamp fecha) { 
        this.fecha = fecha; 
    }

    public String getMotivo() { 
        return motivo; 
    }
    
    public void setMotivo(String motivo) { 
        this.motivo = motivo; 
    }

    public int getIdUsuario() { 
        return idUsuario; 
    }
    
    public void setIdUsuario(int idUsuario) { 
        this.idUsuario = idUsuario; 
    }
    
    public String getNombreProducto() { 
        return nombreProducto; 
    }
    
    public void setNombreProducto(String nombreProducto) { 
        this.nombreProducto = nombreProducto; 
    }

    public String getNombreUsuario() { 
        return nombreUsuario; 
    }
    
    public void setNombreUsuario(String nombreUsuario) { 
        this.nombreUsuario = nombreUsuario; 
    }
}
