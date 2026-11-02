package com.latamairlines.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_pago")
public class Pago {

    // IDENTIFICADOR AUTOMATICO
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "pago_generator"
    )
    @SequenceGenerator(
        name = "pago_generator",
        sequenceName = "tb_pago_seq",
        allocationSize = 1
    )
    @Column(name = "pa_id", nullable = false)
    private Integer id;

    // ATRIBUTOS
    @Column(
        name = "pa_monto",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal monto;

    @Column(name = "pa_fechaPago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "pa_metodoPago", nullable = false)
    private String metodoPago;

    // CADA PAGO PERTENECE A UNA RESERVA
    @OneToOne(optional = false)
    @JoinColumn(
        name = "pa_reserva",
        nullable = false,
        unique = true
    )
    private Reserva reserva;

    // CONSTRUCTORES
    public Pago() {
    }

    public Pago(BigDecimal monto, LocalDateTime fechaPago,
                String metodoPago, Reserva reserva) {
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.reserva = reserva;
    }

    // GETTERS
    public Integer getId() {
        return id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    // SETTERS
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}