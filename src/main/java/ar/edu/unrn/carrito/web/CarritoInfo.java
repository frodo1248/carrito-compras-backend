package ar.edu.unrn.carrito.web;

import java.math.BigDecimal;

public record CarritoInfo(
        Long id,
        Integer cantidadItems,
        BigDecimal total
) {}

