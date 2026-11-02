# LATAM Airlines - Sistema de Gestión de Reservas

Proyecto desarrollado para la asignatura **Persistencia de Datos - Unidad 2** de la Universidad Politécnica Salesiana.

## Descripción

Aplicación de escritorio desarrollada en Java para gestionar reservas aéreas mediante operaciones CRUD, utilizando JPA e Hibernate para la persistencia de datos en PostgreSQL.

## Tecnologías utilizadas

- Java JDK 27
- Apache NetBeans
- Maven
- Jakarta Persistence (JPA)
- Hibernate
- PostgreSQL
- Java Swing

## Funcionalidades

- Crear, buscar, actualizar y eliminar reservas.
- Asociar clientes y vuelos a las reservas.
- Gestionar pagos mediante clases DAO.
- Validar datos y relaciones entre entidades.
- Almacenar información de forma persistente.

## Estructura del proyecto

- `entities`: Cliente, Vuelo, Reserva y Pago.
- `dao`: Operaciones CRUD y acceso a datos.
- `views`: Interfaz gráfica.
- `Main.java`: Inicio de la aplicación.
- `PruebaPagoDAO.java`: Pruebas de gestión de pagos.
- `persistence.xml`: Configuración de JPA.

## Autor

**Juan Xavier Peralta León**  
Ingeniería de Software  
Universidad Politécnica Salesiana
