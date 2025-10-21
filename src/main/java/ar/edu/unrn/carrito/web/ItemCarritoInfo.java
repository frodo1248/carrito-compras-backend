package ar.edu.unrn.carrito.web;

import java.math.BigDecimal;

public record ItemCarritoInfo(
        Long peliculaId,
        String peliculaNombre,
        BigDecimal peliculaPrecio,
        Integer cantidad,
        BigDecimal subtotal
) {}

