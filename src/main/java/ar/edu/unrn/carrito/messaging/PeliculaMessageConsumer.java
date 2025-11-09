package ar.edu.unrn.carrito.messaging;

import ar.edu.unrn.carrito.service.CarritoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PeliculaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PeliculaMessageConsumer.class);
    private final CarritoService carritoService;

    public PeliculaMessageConsumer(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @RabbitListener(queues = "pelicula.agregada.queue")
    public void recibirPeliculaAgregada(PeliculaAgregadaEvent event) {
        logger.info("📥 Mensaje recibido de RabbitMQ - Película: id={}, nombre={}, precio={}",
                     event.id(), event.nombre(), event.precio());

        try {
            carritoService.agregarPeliculaAlCatalogo(event.id(), event.nombre(), event.precio());
            logger.info("✅ Película agregada al catálogo exitosamente: {}", event.nombre());
        } catch (Exception e) {
            logger.error("❌ Error al procesar película desde RabbitMQ: {}", e.getMessage(), e);
        }
    }
}

