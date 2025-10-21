package ar.edu.unrn.carrito.service;

import ar.edu.unrn.carrito.model.Carrito;
import ar.edu.unrn.carrito.model.Pelicula;
import ar.edu.unrn.carrito.web.CarritoDetalle;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

public class CarritoService {
    private final EntityManagerFactory emf;
    private final CatalogoClient catalogoClient;

    public CarritoService(EntityManagerFactory emf, CatalogoClient catalogoClient) {
        this.emf = emf;
        this.catalogoClient = catalogoClient;
    }

    // Obtener el carrito activo (para simplicidad, el primer carrito disponible)
    public Optional<CarritoDetalle> obtenerCarrito() {
        try (var em = emf.createEntityManager()) {
            var carritos = em.createQuery("FROM Carrito ORDER BY fechaCreacion DESC", Carrito.class)
                .setMaxResults(1)
                .getResultList();

            if (carritos.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(carritos.get(0).toCarritoDetalle());
        }
    }

    // Crear un carrito vacío cuando no existe ninguno
    public CarritoDetalle crearCarritoVacio() {
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                Carrito nuevoCarrito = new Carrito();
                em.persist(nuevoCarrito);
                transaction.commit();
                return nuevoCarrito.toCarritoDetalle();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    // Agregar película al carrito desde la BD local
    public void agregarPeliculaDesdeCatalogo(Long peliculaId) {
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                // Buscar la película en la BD local
                Pelicula pelicula = em.find(Pelicula.class, peliculaId);
                if (pelicula == null) {
                    throw new RuntimeException("Película no encontrada");
                }

                // Buscar o crear un carrito activo
                Carrito carrito = obtenerOCrearCarritoActivo(em);

                carrito.agregarPelicula(pelicula, 1); // Cantidad por defecto = 1
                em.merge(carrito);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    private Carrito obtenerOCrearCarritoActivo(jakarta.persistence.EntityManager em) {
        // Por simplicidad, buscamos el primer carrito disponible
        var carritos = em.createQuery("FROM Carrito ORDER BY fechaCreacion DESC", Carrito.class)
            .setMaxResults(1)
            .getResultList();

        if (!carritos.isEmpty()) {
            return carritos.get(0);
        }

        // Si no hay carrito, crear uno nuevo
        Carrito nuevoCarrito = new Carrito();
        em.persist(nuevoCarrito);
        return nuevoCarrito;
    }
}
