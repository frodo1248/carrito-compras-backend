package ar.edu.unrn.carrito.model;

import ar.edu.unrn.carrito.web.CarritoInfo;
import ar.edu.unrn.carrito.web.CarritoDetalle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@Entity
@Table(name = "carritos")
public class Carrito {

    static final String ERROR_CARRITO_VACIO = "No se puede procesar un carrito vacío";
    static final String ERROR_PELICULA_NULA = "La película no puede ser nula";
    static final String ERROR_CANTIDAD_INVALIDA = "La cantidad debe ser mayor a cero";
    static final String ERROR_USUARIO_ID_NULO = "El ID de usuario no puede ser nulo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "carrito_id")
    private List<ItemCarrito> itemsPrivados = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion = LocalDateTime.now();

    // Constructor protegido para JPA
    protected Carrito() {
        // Los campos ya se inicializan con sus valores por defecto
    }

    // Constructor público para que el servicio pueda crear carritos
    public Carrito(String usuarioId) {
        assertUsuarioIdNoNulo(usuarioId);
        this.usuarioId = usuarioId;
    }

    private void assertUsuarioIdNoNulo(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new RuntimeException(ERROR_USUARIO_ID_NULO);
        }
    }

    public void agregarPelicula(Pelicula pelicula, Integer cantidad) {
        assertPeliculaNoNula(pelicula);
        assertCantidadValida(cantidad);

        ItemCarrito itemExistente = buscarItemPorPelicula(pelicula.id());

        if (itemExistente != null) {
            itemExistente.incrementarCantidad(cantidad);
        } else {
            itemsPrivados.add(new ItemCarrito(pelicula, cantidad));
        }

        actualizarFechaModificacion();
    }

    public void actualizarCantidadPelicula(Long peliculaId, Integer nuevaCantidad) {
        assertCantidadValida(nuevaCantidad);

        ItemCarrito item = buscarItemPorPelicula(peliculaId);
        if (item != null) {
            item.actualizarCantidad(nuevaCantidad);
            actualizarFechaModificacion();
        }
    }

    public void eliminarPelicula(Long peliculaId) {
        itemsPrivados.removeIf(item -> item.esDeLaPelicula(peliculaId));
        actualizarFechaModificacion();
    }

    public void vaciar() {
        itemsPrivados.clear();
        actualizarFechaModificacion();
    }

    public BigDecimal calcularTotal() {
        return itemsPrivados.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer cantidadTotalItems() {
        return itemsPrivados.stream()
                .mapToInt(ItemCarrito::cantidad)
                .sum();
    }

    public boolean estaVacio() {
        return itemsPrivados.isEmpty();
    }

    public boolean contienePelicula(Long peliculaId) {
        return buscarItemPorPelicula(peliculaId) != null;
    }

    public void validarParaProcesar() {
        if (estaVacio()) {
            throw new RuntimeException(ERROR_CARRITO_VACIO);
        }
    }

    public List<ItemCarrito> items() {
        return Collections.unmodifiableList(itemsPrivados);
    }

    public String usuarioId() {
        return usuarioId;
    }

    // Métodos para mapear a DTOs siguiendo el patrón del catálogo
    public CarritoInfo toCarritoInfo() {
        return new CarritoInfo(
                id,
                usuarioId,
                cantidadTotalItems(),
                calcularTotal()
        );
    }

    public CarritoDetalle toCarritoDetalle() {
        List<ar.edu.unrn.carrito.web.ItemCarritoInfo> itemsInfo = itemsPrivados.stream()
                .map(ItemCarrito::toItemCarritoInfo)
                .toList();

        return new CarritoDetalle(
                id,
                usuarioId,
                itemsInfo,
                cantidadTotalItems(),
                calcularTotal(),
                fechaCreacion,
                fechaModificacion
        );
    }

    private ItemCarrito buscarItemPorPelicula(Long peliculaId) {
        return itemsPrivados.stream()
                .filter(item -> item.esDeLaPelicula(peliculaId))
                .findFirst()
                .orElse(null);
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

    private void actualizarFechaModificacion() {
        this.fechaModificacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Carrito{" +
                "id=" + id +
                ", cantidadItems=" + itemsPrivados.size() +
                ", total=" + calcularTotal() +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
