package com.boticaestrella.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tomas_tratamiento")
public class TomaTratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_toma")
    private int idToma;

    @Column(name = "id_tratamiento", nullable = false)
    private int idTratamiento;

    @Column(name = "fecha_hora_programada", nullable = false)
    private LocalDateTime fechaHoraProgramada;

    @Column(name = "fecha_hora_real")
    private LocalDateTime fechaHoraReal;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE";

    public TomaTratamiento() {}

    // GETTERS Y SETTERS
    public int getIdToma() { return idToma; }
    public void setIdToma(int idToma) { this.idToma = idToma; }

    public int getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(int idTratamiento) { this.idTratamiento = idTratamiento; }

    public LocalDateTime getFechaHoraProgramada() { return fechaHoraProgramada; }
    public void setFechaHoraProgramada(LocalDateTime fechaHoraProgramada) { this.fechaHoraProgramada = fechaHoraProgramada; }

    public LocalDateTime getFechaHoraReal() { return fechaHoraReal; }
    public void setFechaHoraReal(LocalDateTime fechaHoraReal) { this.fechaHoraReal = fechaHoraReal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}