package com.rota.facil.gateway_service.security.filters;

import com.rota.facil.gateway_service.cache.business.RedisService;
import com.rota.facil.gateway_service.domain.enums.Role;
import com.rota.facil.gateway_service.security.token.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authorization.substring(7);

        if (tokenManager.isValidToken(token)) {
            UUID userId = tokenManager.extractUserId(token);

            if (redisService.getInvalidTokenOfCache(userId) != null) {
                exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
                return exchange.getResponse().setComplete();
            }

            // Extração de dados
            Role role = tokenManager.extractRole(token);
            String email = tokenManager.extractEmail(token);
            UUID prefectureId = tokenManager.extractPrefectureId(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("x-user-id", userId.toString())
                    .header("x-user-role", role.name())
                    .header("x-user-email", email)
                    .header("x-prefecture-id", prefectureId != null ? prefectureId.toString() : "")
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