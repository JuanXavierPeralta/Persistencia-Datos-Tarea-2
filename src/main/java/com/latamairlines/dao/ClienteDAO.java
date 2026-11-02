package com.latamairlines.dao;

import com.latamairlines.entities.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ClienteDAO {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("LatamAirlinesPU");

    // CREAR CLIENTE
    public void crearCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }

        if (cliente.getId() != null) {
            throw new IllegalArgumentException("El cliente ya tiene un ID");
        }

        validarDatos(
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.getCorreo()
        );

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            cliente.setNombre(cliente.getNombre().trim());
            cliente.setIdentificacion(cliente.getIdentificacion().trim());
            cliente.setCorreo(cliente.getCorreo().trim());

            em.persist(cliente);
            em.getTransaction().commit();

            System.out.println("Cliente creado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // BUSCAR CLIENTE POR ID
    public Cliente buscarPorId(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Cliente.class, id);

        } finally {
            em.close();
        }
    }

    // BUSCAR CLIENTE POR IDENTIFICACION
    public Cliente buscarPorIdentificacion(String identificacion) {

        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingrese la identificación del cliente"
            );
        }

        EntityManager em = emf.createEntityManager();

        try {
            List<Cliente> resultados = em.createQuery(
                    "SELECT c FROM Cliente c "
                    + "WHERE c.identificacion = :identificacion",
                    Cliente.class
            )
                    .setParameter("identificacion", identificacion.trim())
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } finally {
            em.close();
        }
    }

    // LISTAR CLIENTES
    public List<Cliente> listarClientes() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Cliente c ORDER BY c.nombre, c.id",
                    Cliente.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR CLIENTE
    public void actualizarCliente(int id, String nombre,
                                  String identificacion, String correo) {

        validarDatos(nombre, identificacion, correo);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Cliente cliente = em.find(Cliente.class, id);

            if (cliente == null) {
                throw new IllegalArgumentException("Cliente no encontrado");
            }

            cliente.setNombre(nombre.trim());
            cliente.setIdentificacion(identificacion.trim());
            cliente.setCorreo(correo.trim());

            em.getTransaction().commit();

            System.out.println("Cliente actualizado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // ELIMINAR CLIENTE
    public void eliminarCliente(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Cliente cliente = em.find(Cliente.class, id);

            if (cliente == null) {
                throw new IllegalArgumentException("Cliente no encontrado");
            }

            // COMPROBAR SI EL CLIENTE TIENE RESERVAS
            Long cantidadReservas = em.createQuery(
                    "SELECT COUNT(r) FROM Reserva r "
                    + "WHERE r.cliente.id = :clienteId",
                    Long.class
            )
                    .setParameter("clienteId", id)
                    .getSingleResult();

            if (cantidadReservas > 0) {
                throw new IllegalArgumentException(
                        "No se puede eliminar un cliente con reservas registradas"
                );
            }

            em.remove(cliente);
            em.getTransaction().commit();

            System.out.println("Cliente eliminado correctamente");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // VALIDAR CAMPOS OBLIGATORIOS
    private void validarDatos(String nombre, String identificacion,
                              String correo) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio"
            );
        }

        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La identificación del cliente es obligatoria"
            );
        }

        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El correo del cliente es obligatorio"
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