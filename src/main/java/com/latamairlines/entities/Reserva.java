package com.latamairlines.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_reserva")
public class Reserva {

    // IDENTIFICADOR AUTOMATICO
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "reserva_generator"
    )
    @SequenceGenerator(
        name = "reserva_generator",
        sequenceName = "tb_reserva_seq",
        allocationSize = 1
    )
    @Column(name = "pr_id", nullable = false)
    private Integer id;

    // ATRIBUTOS
    @Column(name = "pr_numeroPasajeros", nullable = false)
    private Integer numeroPasajeros;

    // MUCHAS RESERVAS PUEDEN PERTENECER A UN CLIENTE
    @ManyToOne(optional = false)
    @JoinColumn(name = "pr_cliente", nullable = false)
    private Cliente cliente;

    // MUCHAS RESERVAS PUEDEN CORRESPONDER A UN VUELO
    @ManyToOne(optional = false)
    @JoinColumn(name = "pr_vuelo", nullable = false)
    private Vuelo vuelo;

    // UNA RESERVA PUEDE TENER UN PAGO
    @OneToOne(mappedBy = "reserva")
    private Pago pago;

    // CONSTRUCTORES
    public Reserva() {
    }

    public Reserva(Cliente cliente, Vuelo vuelo,
                   Integer numeroPasajeros) {
        this.cliente = cliente;
        this.vuelo = vuelo;
        this.numeroPasajeros = numeroPasajeros;
    }

    // GETTERS
    public Integer getId() {
        return id;
    }

    public Integer getNumeroPasajeros() {
        return numeroPasajeros;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public Pago getPago() {
        return pago;
    }

    // SETTERS
    public void setNumeroPasajeros(Integer numeroPasajeros) {
        this.numeroPasajeros = numeroPasajeros;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}