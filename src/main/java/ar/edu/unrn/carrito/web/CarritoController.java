package ar.edu.unrn.carrito.web;

import ar.edu.unrn.carrito.service.CarritoService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/carrito")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    static final String ERROR_CARRITO_NO_ENCONTRADO = "Carrito no encontrado";

    @GetMapping
    public CarritoDetalle obtenerCarrito(@AuthenticationPrincipal Jwt jwt) {
        // Opcional: puedes extraer información del usuario
        // String username = jwt.getClaimAsString("preferred_username");
        // String email = jwt.getClaimAsString("email");

        return carritoService.obtenerCarrito()
                .orElseGet(() -> carritoService.crearCarritoVacio());
    }

    @PostMapping("/agregar/{peliculaId}")
    public CarritoInfo agregarPeliculaDesdeCatalogo(@PathVariable Long peliculaId,
                                                   @AuthenticationPrincipal Jwt jwt) {
        // Opcional: puedes extraer información del usuario
        // String username = jwt.getClaimAsString("preferred_username");

        return carritoService.agregarPeliculaDesdeCatalogo(peliculaId);
    }
}
