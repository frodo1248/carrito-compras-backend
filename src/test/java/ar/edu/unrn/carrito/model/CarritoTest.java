package ar.edu.unrn.carrito.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    @Test
    @DisplayName("Constructor crea carrito vacío correctamente")
    void constructor_creaCarritoVacioCorrectamente() {
        // Setup y Ejercitación: Crear un carrito nuevo
        Carrito carrito = new Carrito();

        // Verificación: Verificar el resultado esperado
        assertTrue(carrito.estaVacio(), "El carrito recién creado debe estar vacío");
        assertEquals(0, carrito.cantidadTotalItems(), "La cantidad total de items debe ser cero");
        assertEquals(BigDecimal.ZERO, carrito.calcularTotal(), "El total debe ser cero");
        assertNotNull(carrito.items(), "La lista de items no debe ser nula");
    }

    @Test
    @DisplayName("AgregarPelicula con datos válidos agrega película correctamente")
    void agregarPelicula_datosValidos_agregaPeliculaCorrectamente() {
        // Setup: Preparar el escenario
        Carrito carrito = new Carrito();
        Pelicula pelicula = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        Integer cantidad = 2;

        // Ejercitación: Ejecutar la acción a probar
        carrito.agregarPelicula(pelicula, cantidad);

        // Verificación: Verificar el resultado esperado
        assertFalse(carrito.estaVacio(), "El carrito no debe estar vacío después de agregar una película");
        assertEquals(1, carrito.items().size(), "Debe haber un item en el carrito");
        assertEquals(cantidad, carrito.cantidadTotalItems(), "La cantidad total debe coincidir");
        assertTrue(carrito.contienePelicula(pelicula.id()), "El carrito debe contener la película agregada");
    }

    @Test
    @DisplayName("ToCarritoInfo mapea correctamente los datos básicos")
    void toCarritoInfo_mapeaCorrectamenteDatosBasicos() {
        // Setup: Preparar el escenario
        Carrito carrito = new Carrito();
        Pelicula pelicula = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        carrito.agregarPelicula(pelicula, 2);

        // Ejercitación: Ejecutar la acción a probar
        var carritoInfo = carrito.toCarritoInfo();

        // Verificación: Verificar el resultado esperado
        assertEquals(carrito.cantidadTotalItems(), carritoInfo.cantidadItems(), "La cantidad de items debe coincidir");
        assertEquals(carrito.calcularTotal(), carritoInfo.total(), "El total debe coincidir");
    }

    @Test
    @DisplayName("ToCarritoDetalle mapea correctamente todos los datos")
    void toCarritoDetalle_mapeaCorrectamenteTodosLosDatos() {
        // Setup: Preparar el escenario
        Carrito carrito = new Carrito();
        Pelicula pelicula1 = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        Pelicula pelicula2 = new Pelicula(2L, "Titanic", new BigDecimal("12.99"));
        carrito.agregarPelicula(pelicula1, 2);
        carrito.agregarPelicula(pelicula2, 1);

        // Ejercitación: Ejecutar la acción a probar
        var carritoDetalle = carrito.toCarritoDetalle();

        // Verificación: Verificar el resultado esperado
        assertEquals(carrito.cantidadTotalItems(), carritoDetalle.cantidadItems(), "La cantidad total debe coincidir");
        assertEquals(carrito.calcularTotal(), carritoDetalle.total(), "El total debe coincidir");
        assertEquals(2, carritoDetalle.items().size(), "Debe haber 2 items en el detalle");
        assertNotNull(carritoDetalle.fechaCreacion(), "La fecha de creación no debe ser nula");
        assertNotNull(carritoDetalle.fechaModificacion(), "La fecha de modificación no debe ser nula");
    }

    // Resto de tests del carrito (mantengo los existentes pero en el nuevo paquete)
    @Test
    @DisplayName("AgregarPelicula con película nula lanza excepción")
    void agregarPelicula_peliculaNula_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Carrito carrito = new Carrito();
        Pelicula pelicula = null;
        Integer cantidad = 2;

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            carrito.agregarPelicula(pelicula, cantidad);
        });
        assertEquals(Carrito.ERROR_PELICULA_NULA, ex.getMessage());
    }

    @Test
    @DisplayName("ValidarParaProcesar con carrito vacío lanza excepción")
    void validarParaProcesar_carritoVacio_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Carrito carrito = new Carrito();

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            carrito.validarParaProcesar();
        });
        assertEquals(Carrito.ERROR_CARRITO_VACIO, ex.getMessage());
    }
}
