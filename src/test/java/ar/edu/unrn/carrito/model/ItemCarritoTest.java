package ar.edu.unrn.carrito.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemCarritoTest {

    @Test
    @DisplayName("Constructor con datos válidos crea item correctamente")
    void constructor_datosValidos_creaItemCorrectamente() {
        // Setup: Preparar el escenario
        Pelicula pelicula = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        Integer cantidad = 2;

        // Ejercitación: Ejecutar la acción a probar
        ItemCarrito item = new ItemCarrito(pelicula, cantidad);

        // Verificación: Verificar el resultado esperado
        assertEquals(pelicula, item.pelicula(), "La película debe coincidir con la asignada");
        assertEquals(cantidad, item.cantidad(), "La cantidad debe coincidir con la asignada");
    }

    @Test
    @DisplayName("ToItemCarritoInfo mapea correctamente los datos")
    void toItemCarritoInfo_mapeaCorrectamenteLosDatos() {
        // Setup: Preparar el escenario
        Pelicula pelicula = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        ItemCarrito item = new ItemCarrito(pelicula, 3);

        // Ejercitación: Ejecutar la acción a probar
        var itemInfo = item.toItemCarritoInfo();

        // Verificación: Verificar el resultado esperado
        assertEquals(pelicula.id(), itemInfo.peliculaId(), "El ID de la película debe coincidir");
        assertEquals(pelicula.nombre(), itemInfo.peliculaNombre(), "El nombre de la película debe coincidir");
        assertEquals(pelicula.precio(), itemInfo.peliculaPrecio(), "El precio de la película debe coincidir");
        assertEquals(item.cantidad(), itemInfo.cantidad(), "La cantidad debe coincidir");
        assertEquals(item.calcularSubtotal(), itemInfo.subtotal(), "El subtotal debe coincidir");
    }

    @Test
    @DisplayName("Constructor con película nula lanza excepción")
    void constructor_peliculaNula_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Pelicula pelicula = null;
        Integer cantidad = 2;

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new ItemCarrito(pelicula, cantidad);
        });
        assertEquals(ItemCarrito.ERROR_PELICULA_NULA, ex.getMessage());
    }

    @Test
    @DisplayName("CalcularSubtotal retorna precio multiplicado por cantidad")
    void calcularSubtotal_retornaPrecioMultiplicadoPorCantidad() {
        // Setup: Preparar el escenario
        BigDecimal precio = new BigDecimal("15.99");
        Pelicula pelicula = new Pelicula(1L, "Avatar", precio);
        ItemCarrito item = new ItemCarrito(pelicula, 3);
        BigDecimal subtotalEsperado = precio.multiply(BigDecimal.valueOf(3));

        // Ejercitación: Ejecutar la acción a probar
        BigDecimal subtotalCalculado = item.calcularSubtotal();

        // Verificación: Verificar el resultado esperado
        assertEquals(subtotalEsperado, subtotalCalculado, "El subtotal debe ser precio × cantidad");
    }
}
