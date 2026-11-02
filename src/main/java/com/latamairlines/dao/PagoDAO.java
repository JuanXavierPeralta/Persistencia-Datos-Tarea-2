package com.latamairlines.dao;

import com.latamairlines.entities.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import com.latamairlines.entities.Pago;



public class PagoDAO {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("LatamAirlinesPU");

    // CREAR PAGO
    public void crearPago(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio");
        }

        if (pago.getId() != null) {
            throw new IllegalArgumentException("El pago ya tiene un ID");
        }

        validarDatos(
                pago.getMonto(),
                pago.getFechaPago(),
                pago.getMetodoPago()
        );

        if (pago.getReserva() == null
                || pago.getReserva().getId() == null) {
            throw new IllegalArgumentException(
                    "Seleccione una reserva registrada"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Reserva reserva = em.find(
                    Reserva.class, pago.getReserva().getId()
            );

            if (reserva == null) {
                throw new IllegalArgumentException("Reserva no encontrada");
            }

            if (reserva.getPago() != null) {
                throw new IllegalArgumentException(
                        "La reserva ya tiene un pago registrado"
                );
            }

            // VINCULAR AMBOS LADOS DE LA RELACION
            pago.setReserva(reserva);
            reserva.setPago(pago);

            pago.setMonto(
                    pago.getMonto().setScale(2, RoundingMode.UNNECESSARY)
            );
            pago.setMetodoPago(pago.getMetodoPago().trim());

            em.persist(pago);
            em.getTransaction().commit();

            System.out.println("Pago creado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // BUSCAR PAGO POR ID
    public Pago buscarPorId(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            List<Pago> resultados = em.createQuery(
                    "SELECT p FROM Pago p "
                    + "JOIN FETCH p.reserva "
                    + "WHERE p.id = :id",
                    Pago.class
            )
                    .setParameter("id", id)
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } finally {
            em.close();
        }
    }

    // BUSCAR PAGO POR RESERVA
    public Pago buscarPorReserva(int reservaId) {

        EntityManager em = emf.createEntityManager();

        try {
            List<Pago> resultados = em.createQuery(
                    "SELECT p FROM Pago p "
                    + "JOIN FETCH p.reserva "
                    + "WHERE p.reserva.id = :reservaId",
                    Pago.class
            )
                    .setParameter("reservaId", reservaId)
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } finally {
            em.close();
        }
    }

    // LISTAR PAGOS DEL MAS RECIENTE AL MAS ANTIGUO
    public List<Pago> listarPagos() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Pago p "
                    + "JOIN FETCH p.reserva "
                    + "ORDER BY p.fechaPago DESC, p.id DESC",
                    Pago.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR DATOS DEL PAGO SIN CAMBIAR SU RESERVA
    public void actualizarPago(int id, BigDecimal monto,
                               LocalDateTime fechaPago,
                               String metodoPago) {

        validarDatos(monto, fechaPago, metodoPago);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Pago pago = em.find(Pago.class, id);

            if (pago == null) {
                throw new IllegalArgumentException("Pago no encontrado");
            }

            pago.setMonto(
                    monto.setScale(2, RoundingMode.UNNECESSARY)
            );
            pago.setFechaPago(fechaPago);
            pago.setMetodoPago(metodoPago.trim());

            em.getTransaction().commit();

            System.out.println("Pago actualizado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // ELIMINAR PAGO
    public void eliminarPago(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Pago pago = em.find(Pago.class, id);

            if (pago == null) {
                throw new IllegalArgumentException("Pago no encontrado");
            }

            // LA RESERVA SE CONSERVA, PERO QUEDA SIN PAGO
            Reserva reserva = pago.getReserva();
            reserva.setPago(null);

            em.remove(pago);
            em.getTransaction().commit();

            System.out.println("Pago eliminado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // CALCULAR EL TOTAL DE LOS PAGOS REGISTRADOS
    public BigDecimal calcularTotalPagos() {

        EntityManager em = emf.createEntityManager();

        try {
            BigDecimal total = em.createQuery(
                    "SELECT SUM(p.monto) FROM Pago p",
                    BigDecimal.class
            ).getSingleResult();

            return total != null ? total : new BigDecimal("0.00");

        } finally {
            em.close();
        }
    }

    // VALIDAR DATOS DEL PAGO
    private void validarDatos(BigDecimal monto,
                              LocalDateTime fechaPago,
                              String metodoPago) {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero"
            );
        }

        // COMPATIBLE CON precision = 12 Y scale = 2
        if (monto.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "El monto supera el máximo permitido"
            );
        }

        try {
            monto.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "El monto debe tener como máximo dos decimales"
            );
        }

        if (fechaPago == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora del pago son obligatorias"
            );
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio"
            );
        }
    }

    // CERRAR RECURSOS
    public void cerrar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}