package ar.edu.unrn.carrito.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarritoDetalle(
        Long id,
        String usuarioId,
        List<ItemCarritoInfo> items,
        Integer cantidadItems,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {
}
