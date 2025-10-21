package ar.edu.unrn.carrito.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CatalogoClient {

    private final RestTemplate restTemplate;
    private final String catalogoBaseUrl = "http://localhost:8080/catalogo";

    @Autowired
    public CatalogoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<PeliculaCatalogoInfo> obtenerPelicula(Long peliculaId) {
        try {
            String url = catalogoBaseUrl + "/" + peliculaId;
            PeliculaDetalleResponse response = restTemplate.getForObject(url, PeliculaDetalleResponse.class);

            if (response == null) {
                return Optional.empty();
            }

            return Optional.of(new PeliculaCatalogoInfo(
                response.id(),
                response.titulo(),
                response.precio()
            ));
        } catch (RestClientException e) {
            // Log del error en un escenario real
            return Optional.empty();
        }
    }

    // Record para mapear la respuesta del catálogo
    public record PeliculaDetalleResponse(
        Long id,
        String titulo,
        int anio,
        BigDecimal precio,
        String director,
        java.util.List<String> actores,
        String condicion,
        String formato,
        String genero,
        String sinopsis,
        String imagen
    ) {}

    // Record con solo los datos que necesita el carrito
    public record PeliculaCatalogoInfo(
        Long id,
        String nombre,
        BigDecimal precio
    ) {}
}
