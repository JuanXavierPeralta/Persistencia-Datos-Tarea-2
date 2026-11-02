package com.latamairlines.dao;

import com.latamairlines.entities.Vuelo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class VueloDAO {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("LatamAirlinesPU");

    // CREAR VUELO
    public void crearVuelo(Vuelo vuelo) {

        if (vuelo == null) {
            throw new IllegalArgumentException("El vuelo es obligatorio");
        }

        if (vuelo.getId() != null) {
            throw new IllegalArgumentException("El vuelo ya tiene un ID");
        }

        validarDatos(
                vuelo.getCodigo(),
                vuelo.getOrigen(),
                vuelo.getDestino(),
                vuelo.getFechaSalida()
        );

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            vuelo.setCodigo(vuelo.getCodigo().trim());
            vuelo.setOrigen(vuelo.getOrigen().trim());
            vuelo.setDestino(vuelo.getDestino().trim());

            em.persist(vuelo);
            em.getTransaction().commit();

            System.out.println("Vuelo creado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // BUSCAR VUELO POR ID
    public Vuelo buscarPorId(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Vuelo.class, id);

        } finally {
            em.close();
        }
    }

    // LISTAR VUELOS POR FECHA DE SALIDA
    public List<Vuelo> listarVuelos() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vuelo v "
                    + "ORDER BY v.fechaSalida, v.id",
                    Vuelo.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // BUSCAR VUELOS POR DESTINO
    public List<Vuelo> buscarPorDestino(String destino) {

        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese un destino");
        }

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vuelo v "
                    + "WHERE LOWER(v.destino) = LOWER(:destino) "
                    + "ORDER BY v.fechaSalida, v.id",
                    Vuelo.class
            )
                    .setParameter("destino", destino.trim())
                    .getResultList();

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR VUELO
    public void actualizarVuelo(int id, String codigo,
                               String origen, String destino,
                               LocalDateTime fechaSalida) {

        validarDatos(codigo, origen, destino, fechaSalida);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Vuelo vuelo = em.find(Vuelo.class, id);

            if (vuelo == null) {
                throw new IllegalArgumentException("Vuelo no encontrado");
            }

            vuelo.setCodigo(codigo.trim());
            vuelo.setOrigen(origen.trim());
            vuelo.setDestino(destino.trim());
            vuelo.setFechaSalida(fechaSalida);

            em.getTransaction().commit();

            System.out.println("Vuelo actualizado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // ELIMINAR VUELO
    public void eliminarVuelo(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Vuelo vuelo = em.find(Vuelo.class, id);

            if (vuelo == null) {
                throw new IllegalArgumentException("Vuelo no encontrado");
            }

            // COMPROBAR SI EL VUELO TIENE RESERVAS
            Long cantidadReservas = em.createQuery(
                    "SELECT COUNT(r) FROM Reserva r "
                    + "WHERE r.vuelo.id = :vueloId",
                    Long.class
            )
                    .setParameter("vueloId", id)
                    .getSingleResult();

            if (cantidadReservas > 0) {
                throw new IllegalArgumentException(
                        "No se puede eliminar un vuelo con reservas registradas"
                );
            }

            em.remove(vuelo);
            em.getTransaction().commit();

            System.out.println("Vuelo eliminado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // VALIDAR DATOS DEL VUELO
    private void validarDatos(String codigo, String origen,
                              String destino, LocalDateTime fechaSalida) {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código del vuelo es obligatorio"
            );
        }

        if (origen == null || origen.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El origen del vuelo es obligatorio"
            );
        }

        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El destino del vuelo es obligatorio"
            );
        }

        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            throw new IllegalArgumentException(
                    "El origen y el destino deben ser diferentes"
            );
        }

        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora de salida son obligatorias"
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