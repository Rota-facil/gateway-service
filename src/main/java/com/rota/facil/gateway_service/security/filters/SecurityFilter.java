package com.rota.facil.gateway_service.security.filters;

import com.rota.facil.gateway_service.cache.business.RedisService;
import com.rota.facil.gateway_service.domain.enums.Role;
import com.rota.facil.gateway_service.security.token.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityFilter implements WebFilter {
    private final TokenManager tokenManager;
    private final RedisService redisService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/user/login",
            "/auth/register",
            "/auth/google/complete-registration",
            "/auth/login/oauth2",
            "/auth/oauth2",
            "/auth/auth/google/success",
            "/auth/health-check",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String path = exchange.getRequest().getURI().getPath();

        if (authorization == null || !authorization.startsWith("Bearer ") || isPublicPath(path) || exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }

        String token = authorization.substring(7);

        if (!tokenManager.isValidToken(token) || redisService.getInvalidTokenOfCache(token) != null) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        if (tokenManager.isValidToken(token)) {
            // Extração de dados
            UUID userId = tokenManager.extractUserId(token);
            Role role = tokenManager.extractRole(token);
            String email = tokenManager.extractEmail(token);
            UUID prefectureId = tokenManager.extractPrefectureId(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("x-user-id", userId.toString())
                    .header("x-user-role", role.name())
                    .header("x-user-email", email)
                    .header("x-prefecture-id", prefectureId != null ? prefectureId.toString() : "")
                    .header("x-user-token", token)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, authorities);

            return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        return chain.filter(exchange);
    }
}