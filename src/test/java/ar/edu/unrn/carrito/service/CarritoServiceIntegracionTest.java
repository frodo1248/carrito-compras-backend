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
import static org.mockito.Mockito.*;

class CarritoServiceIntegracionTest {

    private EntityManagerFactory emf;
    private CarritoService carritoService;
    private CatalogoClient catalogoClientMock;

    @BeforeEach
    void setUp() {
        emf = new EmfBuilder()
                .addClass(Carrito.class)
                .addClass(Pelicula.class)
                .addClass(ar.edu.unrn.carrito.model.ItemCarrito.class)
                .build();

        // Limpiar base de datos antes de cada test
        emf.getSchemaManager().truncate();

        catalogoClientMock = mock(CatalogoClient.class);
        carritoService = new CarritoService(emf, catalogoClientMock);
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con película válida agrega película al carrito")
    void agregarPeliculaDesdeCatalogo_peliculaValida_agregaPeliculaAlCarrito() {
        // Setup: Preparar el escenario
        Long peliculaId = 1L;
        String nombrePelicula = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        CatalogoClient.PeliculaCatalogoInfo peliculaInfo =
            new CatalogoClient.PeliculaCatalogoInfo(peliculaId, nombrePelicula, precio);

        when(catalogoClientMock.obtenerPelicula(peliculaId))
            .thenReturn(Optional.of(peliculaInfo));

        // Ejercitación: Ejecutar la acción a probar
        CarritoInfo resultado = carritoService.agregarPeliculaDesdeCatalogo(peliculaId);

        // Verificación: Verificar el resultado esperado
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(1, resultado.cantidadItems(), "Debe haber un item en el carrito");
        assertEquals(precio, resultado.total(), "El total debe coincidir con el precio de la película");
        verify(catalogoClientMock).obtenerPelicula(peliculaId);
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con película inexistente lanza excepción")
    void agregarPeliculaDesdeCatalogo_peliculaInexistente_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long peliculaIdInexistente = 999L;

        when(catalogoClientMock.obtenerPelicula(peliculaIdInexistente))
            .thenReturn(Optional.empty());

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            carritoService.agregarPeliculaDesdeCatalogo(peliculaIdInexistente);
        });

        assertEquals("Película no encontrada en el catálogo", ex.getMessage(),
            "El mensaje de error debe ser el esperado");
        verify(catalogoClientMock).obtenerPelicula(peliculaIdInexistente);
    }

    @Test
    @DisplayName("AgregarPeliculaDesdeCatalogo con múltiples llamadas incrementa cantidad")
    void agregarPeliculaDesdeCatalogo_multiplasLlamadas_incrementaCantidad() {
        // Setup: Preparar el escenario
        Long peliculaId = 1L;
        String nombrePelicula = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        CatalogoClient.PeliculaCatalogoInfo peliculaInfo =
            new CatalogoClient.PeliculaCatalogoInfo(peliculaId, nombrePelicula, precio);

        when(catalogoClientMock.obtenerPelicula(peliculaId))
            .thenReturn(Optional.of(peliculaInfo));

        // Ejercitación: Ejecutar la acción a probar múltiples veces
        carritoService.agregarPeliculaDesdeCatalogo(peliculaId);
        CarritoInfo resultado = carritoService.agregarPeliculaDesdeCatalogo(peliculaId);

        // Verificación: Verificar el resultado esperado
        assertEquals(2, resultado.cantidadItems(), "Debe haber dos items en el carrito");
        assertEquals(precio.multiply(BigDecimal.valueOf(2)), resultado.total(),
            "El total debe ser el precio multiplicado por 2");
        verify(catalogoClientMock, times(2)).obtenerPelicula(peliculaId);
    }
}
