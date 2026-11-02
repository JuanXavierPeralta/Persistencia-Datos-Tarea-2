package com.latamairlines.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_vuelo")
public class Vuelo {

    // IDENTIFICADOR AUTOMATICO
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "vuelo_generator"
    )
    @SequenceGenerator(
        name = "vuelo_generator",
        sequenceName = "tb_vuelo_seq",
        allocationSize = 1
    )
    @Column(name = "vu_id", nullable = false)
    private Integer id;

    // ATRIBUTOS
    @Column(name = "vu_codigo", nullable = false)
    private String codigo;

    @Column(name = "vu_origen", nullable = false)
    private String origen;

    @Column(name = "vu_destino", nullable = false)
    private String destino;

    @Column(name = "vu_fechaSalida", nullable = false)
    private LocalDateTime fechaSalida;

    // UN VUELO PUEDE TENER VARIAS RESERVAS
    @OneToMany(mappedBy = "vuelo")
    private List<Reserva> reservas = new ArrayList<>();

    // CONSTRUCTORES
    public Vuelo() {
    }

    public Vuelo(String codigo, String origen,
                 String destino, LocalDateTime fechaSalida) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
    }

    // GETTERS
    public Integer getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    // SETTERS
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}