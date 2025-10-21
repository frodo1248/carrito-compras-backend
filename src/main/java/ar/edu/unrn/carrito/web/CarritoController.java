package ar.edu.unrn.carrito.web;

import ar.edu.unrn.carrito.service.CarritoService;
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
    public CarritoDetalle obtenerCarrito() {
        return carritoService.obtenerCarrito()
                .orElseGet(() -> carritoService.crearCarritoVacio());
    }

    @PostMapping("/agregar/{peliculaId}")
    public void agregarPeliculaDesdeCatalogo(@PathVariable Long peliculaId) {
        carritoService.agregarPeliculaDesdeCatalogo(peliculaId);
    }
}
