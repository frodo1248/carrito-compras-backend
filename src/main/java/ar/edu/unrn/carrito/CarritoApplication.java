package ar.edu.unrn.carrito;

import ar.edu.unrn.carrito.model.Carrito;
import ar.edu.unrn.carrito.model.ItemCarrito;
import ar.edu.unrn.carrito.model.Pelicula;
import ar.edu.unrn.carrito.service.CarritoService;
import ar.edu.unrn.carrito.service.CatalogoClient;
import ar.edu.unrn.carrito.utils.EmfBuilder;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
    }
)
@ComponentScan(basePackages = {"ar.edu.unrn.carrito.*"})
public class CarritoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarritoApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public EntityManagerFactory entityManagerFactory() {
        return new EmfBuilder()
                .addClass(Carrito.class)
                .addClass(ItemCarrito.class)
                .addClass(Pelicula.class)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CarritoService carritoService(EntityManagerFactory emf, CatalogoClient catalogoClient) {
        return new CarritoService(emf, catalogoClient);
    }
}
