package ar.edu.unrn.carrito.messaging;

public record PeliculaAgregadaEvent(
    Long id,
    String nombre,
    double precio
) {
}

