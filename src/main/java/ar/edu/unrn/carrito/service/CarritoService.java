package ar.edu.unrn.carrito.service;

import ar.edu.unrn.carrito.model.Carrito;
import ar.edu.unrn.carrito.model.Pelicula;
import ar.edu.unrn.carrito.web.CarritoDetalle;
import ar.edu.unrn.carrito.web.CarritoInfo;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

public class CarritoService {
    private final EntityManagerFactory emf;

    // Mensaje de error usado en tests (visibilidad de paquete)
    static final String ERROR_PELICULA_NO_ENCONTRADA = "Película no encontrada en la base de datos";

    public CarritoService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Obtener el carrito activo de un usuario específico
    public Optional<CarritoDetalle> obtenerCarrito(String usuarioId) {
        try (var em = emf.createEntityManager()) {
            var carritos = em.createQuery("FROM Carrito WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC", Carrito.class)
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(1)
                .getResultList();

            if (carritos.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(carritos.get(0).toCarritoDetalle());
        }
    }

    // Crear un carrito vacío cuando no existe ninguno para el usuario
    public CarritoDetalle crearCarritoVacio(String usuarioId) {
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                Carrito nuevoCarrito = new Carrito(usuarioId);
                em.persist(nuevoCarrito);
                transaction.commit();
                return nuevoCarrito.toCarritoDetalle();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    // Agregar película al carrito desde la base de datos local y devolver información del carrito
    public CarritoInfo agregarPeliculaDesdeCatalogo(Long peliculaId, String usuarioId) {
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                // Buscar la película en la BD local
                Pelicula pelicula = em.find(Pelicula.class, peliculaId);
                if (pelicula == null) {
                    throw new RuntimeException(ERROR_PELICULA_NO_ENCONTRADA);
                }

                // Buscar o crear un carrito activo para el usuario
                Carrito carrito = obtenerOCrearCarritoActivo(em, usuarioId);

                carrito.agregarPelicula(pelicula, 1); // Cantidad por defecto = 1
                // Si el carrito es nuevo fue persistido en obtenerOCrearCarritoActivo; si no, merge
                em.merge(carrito);
                transaction.commit();

                return carrito.toCarritoInfo();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    private Carrito obtenerOCrearCarritoActivo(jakarta.persistence.EntityManager em, String usuarioId) {
        // Buscar el carrito del usuario específico
        var carritos = em.createQuery("FROM Carrito WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC", Carrito.class)
            .setParameter("usuarioId", usuarioId)
            .setMaxResults(1)
            .getResultList();

        if (!carritos.isEmpty()) {
            return carritos.get(0);
        }

        // Si no hay carrito, crear uno nuevo para el usuario
        Carrito nuevoCarrito = new Carrito(usuarioId);
        em.persist(nuevoCarrito);
        return nuevoCarrito;
    }

    // Agregar película al catálogo desde un mensaje de RabbitMQ
    public void agregarPeliculaAlCatalogo(Long id, String nombre, double precio) {
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                // Verificar si la película ya existe
                Pelicula peliculaExistente = em.find(Pelicula.class, id);
                if (peliculaExistente != null) {
                    // Si ya existe, no hacer nada (o actualizar si es necesario)
                    transaction.rollback();
                    return;
                }

                // Crear la nueva película con BigDecimal
                Pelicula nuevaPelicula = new Pelicula(
                    id,
                    nombre,
                    java.math.BigDecimal.valueOf(precio)
                );
                em.persist(nuevaPelicula);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Error al agregar película al catálogo: " + e.getMessage(), e);
            }
        }
    }
}
