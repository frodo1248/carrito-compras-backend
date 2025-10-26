# Carrito de Compras Backend

## Descripción General

Este es un microservicio backend desarrollado en **Java 23** que implementa la funcionalidad de carrito de compras para un sistema de videoclub en una arquitectura de microservicios. El proyecto utiliza el paradigma orientado a objetos con un modelo de dominio rico donde se implementan todas las reglas de negocio.

## Tecnologías Utilizadas

- **Java 23** - Lenguaje de programación
- **Spring Boot 3.4.1** - Framework principal
- **Spring Security** - Seguridad y autenticación OAuth2/JWT
- **Hibernate 7.0.7** - ORM para persistencia
- **MariaDB** - Base de datos principal
- **H2** - Base de datos en memoria para testing
- **JUnit 5** - Framework de testing
- **Maven** - Gestor de dependencias
- **Keycloak** - Servidor de autorización OAuth2

## Arquitectura del Proyecto

### Estructura de Carpetas

```
src/
├── main/java/ar/edu/unrn/carrito/
│   ├── CarritoApplication.java          # Clase principal Spring Boot
│   ├── config/
│   │   └── SecurityConfig.java          # Configuración de seguridad OAuth2
│   ├── model/                           # Modelo de dominio
│   │   ├── Carrito.java                 # Entidad principal del carrito
│   │   ├── ItemCarrito.java             # Items dentro del carrito
│   │   └── Pelicula.java                # Entidad película
│   ├── repository/
│   │   └── CarritoRepository.java       # Repositorio (vacío por diseño)
│   ├── service/
│   │   └── CarritoService.java          # Lógica de negocio
│   ├── utils/
│   │   └── EmfBuilder.java              # Builder para EntityManagerFactory
│   └── web/                             # Capa de presentación
│       ├── CarritoController.java       # Controlador REST
│       ├── CarritoDetalle.java          # DTO con detalle completo
│       ├── CarritoInfo.java             # DTO con información básica
│       └── ItemCarritoInfo.java         # DTO para items del carrito
├── test/java/ar/edu/unrn/carrito/       # Tests unitarios e integración
└── resources/
    └── application.properties           # Configuración de la aplicación
```

## Modelo de Dominio

### Principios de Diseño

- **Sin getters/setters**: Evita objetos anémicos
- **Constructores completos**: Objetos siempre listos para usar
- **Validaciones en constructor**: Garantiza objetos válidos
- **Tell Don't Ask**: Los objetos exponen comportamiento, no estado
- **Encapsulación**: Listas devueltas como solo lectura

### Entidades Principales

#### Carrito
- **Responsabilidades**: 
  - Gestionar items de películas
  - Calcular totales
  - Validar estado para procesamiento
  - Manejar fechas de creación y modificación

- **Operaciones principales**:
  - `agregarPelicula(Pelicula, Integer)`: Agregar película con cantidad
  - `actualizarCantidadPelicula(Long, Integer)`: Modificar cantidad
  - `eliminarPelicula(Long)`: Remover película del carrito
  - `calcularTotal()`: Calcular precio total
  - `validarParaProcesar()`: Validar carrito antes de checkout

#### ItemCarrito
- **Responsabilidades**:
  - Representar una película en el carrito con su cantidad
  - Calcular subtotales
  - Manejar incrementos de cantidad

#### Pelicula
- **Responsabilidades**:
  - Almacenar información básica de la película (id, nombre, precio)
  - Validar integridad de datos

## API REST

### Endpoints Disponibles

#### GET /carrito
- **Descripción**: Obtiene el carrito actual del usuario
- **Seguridad**: Requiere autenticación (ROLE_ADMIN o ROLE_CLIENT)
- **Respuesta**: `CarritoDetalle` con información completa del carrito
- **Comportamiento**: Si no existe carrito, crea uno vacío automáticamente

#### POST /carrito/agregar/{peliculaId}
- **Descripción**: Agrega una película al carrito desde el catálogo local
- **Parámetros**: `peliculaId` - ID de la película a agregar
- **Seguridad**: Requiere autenticación (ROLE_ADMIN o ROLE_CLIENT)
- **Respuesta**: `CarritoInfo` con información básica del carrito actualizado
- **Comportamiento**: Agrega cantidad 1 por defecto, si ya existe incrementa la cantidad

### DTOs (Data Transfer Objects)

#### CarritoInfo
```java
public record CarritoInfo(
    Long id,
    Integer cantidadItems,
    BigDecimal total
)
```

#### CarritoDetalle
```java
public record CarritoDetalle(
    Long id,
    List<ItemCarritoInfo> items,
    Integer cantidadItems,
    BigDecimal total,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaModificacion
)
```

#### ItemCarritoInfo
```java
public record ItemCarritoInfo(
    Long peliculaId,
    String peliculaNombre,
    BigDecimal peliculaPrecio,
    Integer cantidad,
    BigDecimal subtotal
)
```

## Seguridad

### Configuración OAuth2 + JWT

El proyecto implementa seguridad basada en **Keycloak** como servidor de autorización:

- **Autenticación**: JWT tokens emitidos por Keycloak
- **Autorización**: Roles extraídos del JWT (`ROLE_ADMIN`, `ROLE_CLIENT`)
- **CORS**: Configurado para desarrollo con frontend en localhost:5173
- **Endpoints protegidos**: Todos los endpoints `/carrito/**` requieren autenticación

### Extracción de Roles

El sistema extrae roles desde dos ubicaciones en el JWT:
1. `realm_access.roles` (roles del realm)
2. `resource_access.test-api.roles` (roles específicos del cliente)

## Persistencia

### Configuración de Base de Datos

- **Desarrollo/Producción**: MariaDB en localhost:3306
- **Testing**: H2 en memoria
- **ORM**: Hibernate con configuración programática (sin persistence.xml)

### EmfBuilder

Utility class que centraliza la configuración del EntityManagerFactory:

```java
// Para desarrollo
new EmfBuilder()
    .addClass(Carrito.class)
    .addClass(ItemCarrito.class)
    .addClass(Pelicula.class)
    .build();

// Para tests
new EmfBuilder()
    .memory()  // Cambia a H2 en memoria
    .addClass(...)
    .build();
```

## Testing

### Estrategia de Testing

#### Tests Unitarios
- **Ubicación**: `src/test/java/.../model/`
- **Herramientas**: JUnit 5.13
- **Cobertura**: Todas las entidades del modelo de dominio
- **Principios**:
  - Un caso de prueba por test
  - Nombres descriptivos con patrón `accion_resultado`
  - Estructura: Setup → Ejercitación → Verificación
  - Sin mocks (código real en memoria)

#### Tests de Integración
- **Ubicación**: `src/test/java/.../service/`
- **Base de datos**: H2 en memoria
- **Configuración**: Truncate de BD en `@BeforeEach`
- **Cobertura**: Servicios con acceso a base de datos

### Ejemplos de Tests

```java
@Test
@DisplayName("AgregarPelicula con datos válidos agrega película correctamente")
void agregarPelicula_datosValidos_agregaPeliculaCorrectamente() {
    // Setup: Preparar el escenario
    Carrito carrito = new Carrito();
    Pelicula pelicula = new Pelicula(1L, "Avatar", new BigDecimal("15.99"));
    
    // Ejercitación: Ejecutar la acción a probar
    carrito.agregarPelicula(pelicula, 2);
    
    // Verificación: Verificar el resultado esperado
    assertFalse(carrito.estaVacio());
    assertEquals(2, carrito.cantidadTotalItems());
    assertTrue(carrito.contienePelicula(1L));
}
```

## Configuración y Ejecución

### Requisitos Previos

1. **Java 23** instalado
2. **MariaDB** ejecutándose en localhost:3306
3. **Keycloak** ejecutándose en localhost:9090
4. Base de datos `carrito` creada en MariaDB

### Configuración de Keycloak

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/videoclub
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:9090/realms/videoclub/protocol/openid-connect/certs
```

### Ejecución

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Estado Actual del Desarrollo

### ✅ Implementado

- [x] Modelo de dominio completo (Carrito, ItemCarrito, Pelicula)
- [x] Validaciones de negocio en constructores
- [x] Persistencia con Hibernate
- [x] API REST básica (obtener carrito, agregar película)
- [x] Seguridad OAuth2 con Keycloak
- [x] Tests unitarios del modelo de dominio
- [x] Tests de integración del servicio
- [x] Configuración de CORS para frontend
- [x] DTOs para comunicación con frontend

### 🚧 En Desarrollo / Pendiente

Las siguientes funcionalidades pueden ser implementadas según las necesidades:

- [ ] Actualizar cantidad de película en carrito
- [ ] Eliminar película específica del carrito
- [ ] Vaciar carrito completo
- [ ] Gestión de usuarios (carritos por usuario)
- [ ] Sincronización con microservicio de catálogo
- [ ] Proceso de checkout/finalización de compra
- [ ] Auditoría y logging de operaciones
- [ ] Métricas y monitoreo
- [ ] Documentación OpenAPI/Swagger

## Arquitectura de Microservicios

Este backend del carrito está diseñado para integrarse en una arquitectura de microservicios más amplia del videoclub:

- **Independiente**: Base de datos propia, no comparte estado
- **Comunicación**: REST API para frontend, preparado para comunicación inter-servicios
- **Seguridad**: Integrado con servidor central de autenticación (Keycloak)
- **Escalabilidad**: Diseño stateless, preparado para múltiples instancias

## Contribución

El proyecto sigue estrictas convenciones de código y testing. Para contribuir:

1. Mantener el modelo de dominio rico sin getters/setters
2. Todas las validaciones en constructores
3. Usar RuntimeException para errores de negocio
4. Mantener cobertura de tests unitarios
5. Seguir convenciones de naming para tests: `accion_resultado`
6. Usar `@DisplayName` descriptivo en tests
