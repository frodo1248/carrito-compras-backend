package ar.edu.unrn.carrito.service;

import ar.edu.unrn.carrito.model.Carrito;
import ar.edu.unrn.carrito.model.Pelicula;
import ar.edu.unrn.carrito.utils.EmfBuilder;
import ar.edu.unrn.carrito.web.CarritoInfo;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class CarritoServiceIntegracionTest {

    private EntityManagerFactory emf;
    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        emf = new EmfBuilder()
                .addClass(Carrito.class)
                .addClass(Pelicula.class)
                .addClass(ar.edu.unrn.carrito.model.ItemCarrito.class)
                .build();

        // Limpiar base de datos antes de cada test
        emf.getSchemaManager().truncate();

        carritoService = new CarritoService(emf);
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con película válida agrega película al carrito")
    void agregarPeliculaDesdeCatalogo_peliculaValida_agregaPeliculaAlCarrito() {
        // Setup: Preparar el escenario
        Long peliculaId = 1L;
        String nombrePelicula = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        // Crear película en la base de datos
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            Pelicula pelicula = new Pelicula(peliculaId, nombrePelicula, precio);
            em.persist(pelicula);
            transaction.commit();
        }

        // Ejercitación: Ejecutar la acción a probar
        CarritoInfo resultado = carritoService.agregarPeliculaDesdeCatalogo(peliculaId);

        // Verificación: Verificar el resultado esperado
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(1, resultado.cantidadItems(), "Debe haber un item en el carrito");
        assertEquals(precio, resultado.total(), "El total debe coincidir con el precio de la película");
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con película inexistente lanza excepción")
    void agregarPeliculaDesdeCatalogo_peliculaInexistente_lanzaExcepcion() {
        // Setup: Preparar el escenario - no creamos ninguna película en la BD
        Long peliculaIdInexistente = 999L;

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            carritoService.agregarPeliculaDesdeCatalogo(peliculaIdInexistente);
        });

        assertEquals(CarritoService.ERROR_PELICULA_NO_ENCONTRADA, ex.getMessage(),
            "El mensaje de error debe ser el esperado");
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con múltiples llamadas incrementa cantidad")
    void agregarPeliculaDesdeCatalogo_multiplasLlamadas_incrementaCantidad() {
        // Setup: Preparar el escenario
        Long peliculaId = 1L;
        String nombrePelicula = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        // Crear película en la base de datos
        try (var em = emf.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            Pelicula pelicula = new Pelicula(peliculaId, nombrePelicula, precio);
            em.persist(pelicula);
            transaction.commit();
        }

        // Ejercitación: Ejecutar la acción a probar múltiples veces
        carritoService.agregarPeliculaDesdeCatalogo(peliculaId);
        CarritoInfo resultado = carritoService.agregarPeliculaDesdeCatalogo(peliculaId);

        // Verificación: Verificar el resultado esperado
        assertEquals(2, resultado.cantidadItems(), "Debe haber dos items en el carrito");
        assertEquals(precio.multiply(BigDecimal.valueOf(2)), resultado.total(),
            "El total debe ser el precio multiplicado por 2");
    }
}
