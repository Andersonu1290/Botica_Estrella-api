package com.boticaestrella.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tratamientos_smartcare")
public class TratamientoSmartCare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Integer idTratamiento;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    // AHORA PERMITE NULL (Integer en vez de int)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre_medicamento", nullable = false, length = 150)
    private String nombreMedicamento;

    @Column(name = "dosis", nullable = false, length = 60)
    private String dosis;

    // NUEVOS CAMPOS
    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Column(name = "cantidad_por_toma", nullable = false)
    private int cantidadPorToma = 1;

    @Column(name = "frecuencia_horas", nullable = false)
    private int frecuenciaHoras;

    @Column(name = "cantidad_inicial", nullable = false)
    private int cantidadInicial;

    @Column(name = "stock_actual", nullable = false)
    private int stockActual;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "hora_primera_dosis", nullable = false, length = 5)
    private String horaPrimeraDosis;

    @Column(name = "dias_restantes", nullable = false)
    private int diasRestantes;

    @Column(name = "fecha_agotamiento", nullable = false)
    private LocalDate fechaAgotamiento;

    @Column(name = "texto_ocr_crudo", columnDefinition = "TEXT")
    private String textoOcrCrudo;

    @Column(name = "validado_por_usuario", nullable = false)
    private boolean validadoPorUsuario = true;

    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO";

    @Transient
    private boolean alertaCritica;

    public TratamientoSmartCare() {}

    // GETTERS Y SETTERS
    public Integer getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(Integer idTratamiento) { this.idTratamiento = idTratamiento; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    
    public String getNombreMedicamento() { return nombreMedicamento; }
    public void setNombreMedicamento(String nombreMedicamento) { this.nombreMedicamento = nombreMedicamento; }
    
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public int getCantidadPorToma() { return cantidadPorToma; }
    public void setCantidadPorToma(int cantidadPorToma) { this.cantidadPorToma = cantidadPorToma; }
    
    public int getFrecuenciaHoras() { return frecuenciaHoras; }
    public void setFrecuenciaHoras(int frecuenciaHoras) { this.frecuenciaHoras = frecuenciaHoras; }
    
    public int getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(int cantidadInicial) { this.cantidadInicial = cantidadInicial; }
    
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public String getHoraPrimeraDosis() { return horaPrimeraDosis; }
    public void setHoraPrimeraDosis(String horaPrimeraDosis) { this.horaPrimeraDosis = horaPrimeraDosis; }
    
    public int getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(int diasRestantes) { this.diasRestantes = diasRestantes; }
    
    public LocalDate getFechaAgotamiento() { return fechaAgotamiento; }
    public void setFechaAgotamiento(LocalDate fechaAgotamiento) { this.fechaAgotamiento = fechaAgotamiento; }
    
    public String getTextoOcrCrudo() { return textoOcrCrudo; }
    public void setTextoOcrCrudo(String textoOcrCrudo) { this.textoOcrCrudo = textoOcrCrudo; }
    
    public boolean isValidadoPorUsuario() { return validadoPorUsuario; }
    public void setValidadoPorUsuario(boolean validadoPorUsuario) { this.validadoPorUsuario = validadoPorUsuario; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public boolean isAlertaCritica() { return alertaCritica; }
    public void setAlertaCritica(boolean alertaCritica) { this.alertaCritica = alertaCritica; }
}