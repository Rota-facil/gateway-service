package com.rota.facil.gateway_service.security.config;

import com.rota.facil.gateway_service.security.filters.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> {})
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterBefore(securityFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/webjars/**", "/*/v3/api-docs").permitAll()
                        .pathMatchers("/*/v3/api-docs/**", "/*/swagger-ui/**", "/*/swagger-ui.html").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/auth/health-check").permitAll()
                        .pathMatchers("/transports/health-check").permitAll()
                        .pathMatchers("/files/health-check").permitAll()
                        .pathMatchers("/places/health-check").permitAll()
                        .pathMatchers("/audit/health-check").permitAll()
                        .pathMatchers("/locations/health-check").permitAll()

                        .pathMatchers("/auth/user/login").permitAll()
                        .pathMatchers("/auth/register/**").permitAll()
                        .pathMatchers("/auth/google/complete-registration").permitAll()
                        .pathMatchers("/auth/login/oauth2/**").permitAll()
                        .pathMatchers("/auth/oauth2/**").permitAll()
                        .pathMatchers("/auth/auth/google/success").permitAll()

                        .pathMatchers(HttpMethod.GET, "/places/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/auth/prefectures/**").permitAll()

                        .pathMatchers("/places/**").hasAnyRole("ADMIN", "SUPERUSER")
                        .pathMatchers("/audit/**").hasAnyRole("ADMIN", "SUPERUSER")
                        .pathMatchers("/transports/metrics/**").hasAnyRole("ADMIN", "SUPERUSER")

                        .pathMatchers("/auth/user/prefecture/register").hasRole("SUPERUSER")
                        .pathMatchers("/auth/prefectures/**").hasRole("SUPERUSER")


                        .pathMatchers("/auth/driver/register").hasRole("ADMIN")
                        .pathMatchers("/auth/user/prefecture/register").hasRole("ADMIN")

                        .pathMatchers("/transports/trips/{tripId}/join").hasAnyRole("STUDENT")
                        .pathMatchers("/transports/trips/{tripId}/exit").hasAnyRole("STUDENT")
                        .pathMatchers("/transports/trips/{tripId}/checkin").hasAnyRole("STUDENT")
                        .pathMatchers("/auth/user/prefecture/{prefectureId}/change").hasAnyRole("STUDENT")

                        .pathMatchers("/transports/trips/my-trips").hasAnyRole("STUDENT", "DRIVER")

                        .pathMatchers("/transports/trips/{tripId}/init").hasAnyRole("DRIVER")
                        .pathMatchers("/transports/trips/{tripId}/cancel").hasAnyRole("DRIVER")

                        .pathMatchers("/transports/routes/register").hasAnyRole("ADMIN")
                        .pathMatchers("/transports/trips/register").hasAnyRole("ADMIN")
                        .pathMatchers("/transports/bus/register").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/transports/bus/{busId}").hasAnyRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .build();
    }
}
