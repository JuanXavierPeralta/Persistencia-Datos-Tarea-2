package com.latamairlines.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    // IDENTIFICADOR AUTOMATICO
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "cliente_generator"
    )
    @SequenceGenerator(
        name = "cliente_generator",
        sequenceName = "tb_cliente_seq",
        allocationSize = 1
    )
    @Column(name = "cl_id", nullable = false)
    private Integer id;

    // ATRIBUTOS
    @Column(name = "cl_nombre", nullable = false)
    private String nombre;

    @Column(name = "cl_identificacion", nullable = false, unique = true)
    private String identificacion;

    @Column(name = "cl_correo", nullable = false)
    private String correo;

    // UN CLIENTE PUEDE TENER VARIAS RESERVAS
    @OneToMany(mappedBy = "cliente")
    private List<Reserva> reservas = new ArrayList<>();

    // CONSTRUCTORES
    public Cliente() {
    }

    public Cliente(String nombre, String identificacion,
                   String correo) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.correo = correo;
    }

    // GETTERS
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    // SETTERS
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}