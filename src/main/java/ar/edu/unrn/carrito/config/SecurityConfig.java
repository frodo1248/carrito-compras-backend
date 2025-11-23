package ar.edu.unrn.carrito.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("🔒 Configurando SecurityFilterChain con Keycloak OAuth2");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos (para health checks, actuator, etc.)
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Endpoints del carrito - requieren estar autenticado con ROLE_ADMIN o ROLE_CLIENT
                        .requestMatchers("/**").hasAnyRole("ADMIN", "CLIENT")
                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        // Configurar para extraer roles desde realm_access.roles en el JWT
//        authoritiesConverter.setAuthorityPrefix("ROLE_");
//
//        // Crear un converter personalizado que puede manejar la estructura anidada de Keycloak
//        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
//        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            // Intentar extraer desde realm_access.roles
//            var realmAccess = jwt.getClaimAsMap("realm_access");
//            if (realmAccess != null && realmAccess.containsKey("roles")) {
//                var roles = (java.util.List<String>) realmAccess.get("roles");
//                logger.debug("Roles encontrados en realm_access: {}", roles);
//                return roles.stream()
//                        .map(role -> {
//                            // Si el rol ya tiene el prefijo ROLE_, no lo duplicamos
//                            String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
//                            return new org.springframework.security.core.authority.SimpleGrantedAuthority(finalRole);
//                        })
//                        .collect(java.util.stream.Collectors.toList());
//            }
//
//            // Fallback: intentar extraer desde resource_access
//            var resourceAccess = jwt.getClaimAsMap("resource_access");
//            if (resourceAccess != null && resourceAccess.containsKey("test-api")) {
//                var clientAccess = (java.util.Map<String, Object>) resourceAccess.get("test-api");
//                if (clientAccess != null && clientAccess.containsKey("roles")) {
//                    var roles = (java.util.List<String>) clientAccess.get("roles");
//                    logger.debug("Roles encontrados en resource_access.test-api: {}", roles);
//                    return roles.stream()
//                            .map(role -> {
//                                // Si el rol ya tiene el prefijo ROLE_, no lo duplicamos
//                                String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
//                                return new org.springframework.security.core.authority.SimpleGrantedAuthority(finalRole);
//                            })
//                            .collect(java.util.stream.Collectors.toList());
//                }
//            }
//
//            // Si no encuentra roles, devolver lista vacía
//            logger.warn("No se encontraron roles válidos en el JWT");
//            return java.util.Collections.emptyList();
//        });
//
//        return authenticationConverter;
//    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extraer roles desde realm_access.roles
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roles = (java.util.List<String>) realmAccess.get("roles");
                logger.debug("Roles encontrados: {}", roles);
                return roles.stream()
                        .map(role -> {
                            // Si el rol ya tiene el prefijo ROLE_, no lo duplicamos
                            String finalRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            return new org.springframework.security.core.authority.SimpleGrantedAuthority(finalRole);
                        })
                        .collect(java.util.stream.Collectors.toList());
            }

            logger.warn("No se encontraron roles en realm_access");
            return java.util.Collections.emptyList();
        });

        return authenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
