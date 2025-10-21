package ar.edu.unrn.carrito.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PeliculaTest {

    @Test
    @DisplayName("Constructor con datos válidos crea película correctamente")
    void constructor_datosValidos_creaPeliculaCorrectamente() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        // Ejercitación: Ejecutar la acción a probar
        Pelicula pelicula = new Pelicula(id, nombre, precio);

        // Verificación: Verificar el resultado esperado
        assertEquals(id, pelicula.id(), "El ID debe coincidir con el valor asignado");
        assertEquals(nombre, pelicula.nombre(), "El nombre debe coincidir con el valor asignado");
        assertEquals(precio, pelicula.precio(), "El precio debe coincidir con el valor asignado");
    }

    @Test
    @DisplayName("Constructor con ID nulo lanza excepción")
    void constructor_idNulo_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = null;
        String nombre = "Avatar";
        BigDecimal precio = new BigDecimal("15.99");

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_ID_NULO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con nombre nulo lanza excepción")
    void constructor_nombreNulo_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = null;
        BigDecimal precio = new BigDecimal("15.99");

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_NOMBRE_NULO_O_VACIO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con nombre vacío lanza excepción")
    void constructor_nombreVacio_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "";
        BigDecimal precio = new BigDecimal("15.99");

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_NOMBRE_NULO_O_VACIO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con nombre solo espacios lanza excepción")
    void constructor_nombreSoloEspacios_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "   ";
        BigDecimal precio = new BigDecimal("15.99");

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_NOMBRE_NULO_O_VACIO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con precio nulo lanza excepción")
    void constructor_precioNulo_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "Avatar";
        BigDecimal precio = null;

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_PRECIO_NULO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con precio negativo lanza excepción")
    void constructor_precioNegativo_lanzaExcepcion() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "Avatar";
        BigDecimal precio = new BigDecimal("-5.00");

        // Ejercitación y Verificación: Ejecutar la acción y verificar la excepción
        var ex = assertThrows(RuntimeException.class, () -> {
            new Pelicula(id, nombre, precio);
        });
        assertEquals(Pelicula.ERROR_PRECIO_NEGATIVO, ex.getMessage());
    }

    @Test
    @DisplayName("Constructor con precio cero crea película correctamente")
    void constructor_precioCero_creaPeliculaCorrectamente() {
        // Setup: Preparar el escenario
        Long id = 1L;
        String nombre = "Pelicula Gratis";
        BigDecimal precio = BigDecimal.ZERO;

        // Ejercitación: Ejecutar la acción a probar
        Pelicula pelicula = new Pelicula(id, nombre, precio);

        // Verificación: Verificar el resultado esperado
        assertNotNull(pelicula, "La película debe crearse correctamente con precio cero");
        assertEquals(BigDecimal.ZERO, pelicula.precio(), "El precio debe ser cero");
    }

    @Test
    @DisplayName("Equals compara correctamente por ID")
    void equals_comparaPorId_funcionaCorrectamente() {
        // Setup: Preparar el escenario
        Pelicula pelicula1 = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        Pelicula pelicula2 = new Pelicula(1L, "Nombre Diferente", new BigDecimal("10.00"));
        Pelicula pelicula3 = new Pelicula(2L, "Avatar", new BigDecimal("15.99"));

        // Ejercitación y Verificación: Ejecutar las comparaciones
        assertEquals(pelicula1, pelicula2, "Películas con mismo ID deben ser iguales");
        assertNotEquals(pelicula1, pelicula3, "Películas con diferente ID deben ser diferentes");
        assertEquals(pelicula1, pelicula1, "Una película debe ser igual a sí misma");
    }

    @Test
    @DisplayName("HashCode es consistente con equals")
    void hashCode_consistenteConEquals_funcionaCorrectamente() {
        // Setup: Preparar el escenario
        Pelicula pelicula1 = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
        Pelicula pelicula2 = new Pelicula(1L, "Nombre Diferente", new BigDecimal("10.00"));

        // Ejercitación y Verificación: Verificar consistencia de hashCode
        assertEquals(pelicula1.hashCode(), pelicula2.hashCode(),
                "HashCode debe ser igual para objetos que son equals");
    }
}
