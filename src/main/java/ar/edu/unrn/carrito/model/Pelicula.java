package ar.edu.unrn.carrito.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "peliculas")
public class Pelicula {

    static final String ERROR_ID_NULO = "El id de la película no puede ser nulo";
    static final String ERROR_NOMBRE_NULO_O_VACIO = "El nombre de la película no puede ser nulo o vacío";
    static final String ERROR_PRECIO_NULO = "El precio de la película no puede ser nulo";
    static final String ERROR_PRECIO_NEGATIVO = "El precio de la película no puede ser negativo";

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public Pelicula(Long id, String nombre, BigDecimal precio) {
        assertIdNoNulo(id);
        assertNombreValido(nombre);
        assertPrecioValido(precio);

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public BigDecimal precio() {
        return precio;
    }

    private void assertIdNoNulo(Long id) {
        if (id == null) {
            throw new RuntimeException(ERROR_ID_NULO);
        }
    }

    private void assertNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException(ERROR_NOMBRE_NULO_O_VACIO);
        }
    }

    private void assertPrecioValido(BigDecimal precio) {
        if (precio == null) {
            throw new RuntimeException(ERROR_PRECIO_NULO);
        }
        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(ERROR_PRECIO_NEGATIVO);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pelicula pelicula = (Pelicula) obj;
        return id.equals(pelicula.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}
