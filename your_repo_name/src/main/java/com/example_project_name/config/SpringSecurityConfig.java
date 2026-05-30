package com.example_project_name.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // No CSRF tokens, because this is a stateless JWT API.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Define authorization rules for endpoints and roles here. Adjust as needed.
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll() // open endpoints
                .requestMatchers("/secured/admin").hasRole("ADMIN")
                .requestMatchers("/secured/user").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll())

            // validate incoming bearer tokens as JWTs.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(Environment environment) {
        // can use multiple trusted issuers configured via spring.security.oauth2.resourceserver.jwt.issuer-uris
        List<String> issuerUris = Binder.get(environment)
                .bind("spring.security.oauth2.resourceserver.jwt.issuer-uris", Bindable.listOf(String.class))
                .orElse(List.of());

        // alternatively a single Keycloak issuer defined via keycloak.* properties.
        String keycloakAuthServerUrl = Binder.get(environment)
                .bind("keycloak.auth-server-url", String.class)
                .orElse(null);
        String keycloakRealm = Binder.get(environment)
                .bind("keycloak.realm", String.class)
                .orElse(null);

        if (keycloakAuthServerUrl != null && keycloakRealm != null) {
            String keycloakIssuer = buildKeycloakIssuer(keycloakAuthServerUrl, keycloakRealm);
            if (issuerUris.stream().noneMatch(uri -> normalizeIssuerUri(uri).equals(normalizeIssuerUri(keycloakIssuer)))) {
                issuerUris = Stream.concat(issuerUris.stream(), Stream.of(keycloakIssuer)).toList();
            }
        }

        if (issuerUris.isEmpty()) { //no issuers
            throw new IllegalStateException("Missing issuer URIs configuration and no Keycloak issuer configured");
        }

        Map<String, JwtDecoder> decoders = issuerUris.stream()
                .map(this::normalizeIssuerUri)
                .collect(Collectors.toUnmodifiableMap(
                        issuer -> issuer,
                        JwtDecoders::fromIssuerLocation,
                        (existing, replacement) -> existing));

        return token -> {
            String issuer = getIssuer(token);
            JwtDecoder decoder = decoders.get(normalizeIssuerUri(issuer));
            if (decoder == null) {
                throw new BadJwtException("Untrusted issuer: " + issuer);
            }
            return decoder.decode(token);
        };
    }


    //Helper method to construct Keycloak issuer URI from auth server URL and realm
    private String buildKeycloakIssuer(String authServerUrl, String realm) {
        String normalized = authServerUrl.endsWith("/") ? authServerUrl.substring(0, authServerUrl.length() - 1) : authServerUrl;
        return normalized + "/realms/" + realm;
    }

    //Helper method to extract issuer without decoding entire jwt to determine which decoder to use
    private String getIssuer(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new BadJwtException("Invalid JWT format");
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode json = new ObjectMapper().readTree(new String(decoded, StandardCharsets.UTF_8));
            JsonNode issuer = json.get("iss");
            if (issuer == null || issuer.asText().isEmpty()) {
                throw new BadJwtException("Missing issuer claim");
            }
            return issuer.asText();
        } catch (Exception ex) {
            throw new BadJwtException("Unable to parse JWT issuer", ex);
        }
    }

    //Helper merthod to normalize issuer URIs for consistent comparison 
    private String normalizeIssuerUri(String issuer) {
        return issuer.endsWith("/") ? issuer.substring(0, issuer.length() - 1) : issuer;
    }
}
