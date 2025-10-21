package ar.edu.unrn.carrito.model;

import ar.edu.unrn.carrito.web.ItemCarritoInfo;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    static final String ERROR_PELICULA_NULA = "La película no puede ser nula";
    static final String ERROR_CANTIDAD_INVALIDA = "La cantidad debe ser mayor a cero";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;

    @Column(nullable = false)
    private Integer cantidad;

    public ItemCarrito(Pelicula pelicula, Integer cantidad) {
        assertPeliculaNoNula(pelicula);
        assertCantidadValida(cantidad);

        this.pelicula = pelicula;
        this.cantidad = cantidad;
    }

    private void assertPeliculaNoNula(Pelicula pelicula) {
        if (pelicula == null) {
            throw new RuntimeException(ERROR_PELICULA_NULA);
        }
    }

    private void assertCantidadValida(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException(ERROR_CANTIDAD_INVALIDA);
        }
    }

    public Integer cantidad() {
        return cantidad;
    }

    public Pelicula pelicula() {
        return pelicula;
    }

    public void incrementarCantidad(Integer cantidadAdicional) {
        assertCantidadValida(cantidadAdicional);
        this.cantidad += cantidadAdicional;
    }

    public void actualizarCantidad(Integer nuevaCantidad) {
        assertCantidadValida(nuevaCantidad);
        this.cantidad = nuevaCantidad;
    }

    public BigDecimal calcularSubtotal() {
        return pelicula.precio().multiply(BigDecimal.valueOf(cantidad));
    }

    public boolean esDeLaPelicula(Long peliculaId) {
        return pelicula.id().equals(peliculaId);
    }

    // Método para mapear a DTO siguiendo el patrón del catálogo
    public ItemCarritoInfo toItemCarritoInfo() {
        return new ItemCarritoInfo(
                pelicula.id(),
                pelicula.nombre(),
                pelicula.precio(),
                cantidad,
                calcularSubtotal()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemCarrito that = (ItemCarrito) obj;
        return pelicula.equals(that.pelicula);
    }

    @Override
    public int hashCode() {
        return pelicula.hashCode();
    }

    @Override
    public String toString() {
        return "ItemCarrito{" +
                "id=" + id +
                ", pelicula=" + pelicula.nombre() +
                ", cantidad=" + cantidad +
                ", subtotal=" + calcularSubtotal() +
                '}';
    }
}
