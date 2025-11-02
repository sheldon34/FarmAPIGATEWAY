package org.example.farmapigateway.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity

public class SecurityConfig {
    private final JwtAuthenticationConverter jwtAuthConverter;
    public SecurityConfig(JwtAuthenticationConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Value("${okta.oauth2.issuer}")
    private String issuer;

    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ServerLogoutSuccessHandler logoutSuccessHandler) {

        http
                 .csrf(ServerHttpSecurity.CsrfSpec::disable)
                 .authorizeExchange(authorize -> authorize
                         .pathMatchers("/cart/**").authenticated()
                         .anyExchange().permitAll()
                 )
                 .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler))
                 .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        // adapt the synchronous JwtAuthenticationConverter to the reactive API
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter)))
                );

         return http.build();
     }

    // expose the adapter as a bean (optional) so tests can reuse it
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverterAdapter() {
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter);
    }

     @Bean
     public ServerLogoutSuccessHandler logoutSuccessHandler() {
         String returnTo = "http://localhost:3000/";
         String logoutUrl = UriComponentsBuilder
                 .fromHttpUrl(issuer + "/v1/logout?client_id={clientId}&returnTo={returnTo}")
                 .encode()
                 .buildAndExpand(clientId, returnTo)
                 .toUriString();

         RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
         handler.setLogoutSuccessUrl(URI.create(logoutUrl));
         return handler;
     }
 }
