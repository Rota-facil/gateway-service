package com.rota.facil.gateway_service.security.config;

import com.rota.facil.gateway_service.security.filters.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterBefore(securityFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/health-check").permitAll()
                        .pathMatchers("/transports/health-check").permitAll()
                        .pathMatchers("/files/health-check").permitAll()
                        .pathMatchers("/places/health-check").permitAll()
                        .pathMatchers("/audit/health-check").permitAll()
                        .pathMatchers("/locations/health-check").permitAll()

                        .pathMatchers("/auth/login/**").permitAll()
                        .pathMatchers("/auth/register/**").permitAll()

                        .pathMatchers("/places/**").hasAnyRole("ADMIN", "SUPERUSER")
                        .pathMatchers("/audit/**").hasAnyRole("ADMIN", "SUPERUSER")
                        .anyExchange().authenticated()
                )
                .build();
    }
}
