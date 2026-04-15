package com.rota.facil.gateway_service.security.token;

import com.rota.facil.gateway_service.domain.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenManager {
    private final PublicKey publicKey;

    public UUID extractUserId(String token) {
        return this.extractClaims(token, claims -> claims.get("userId", UUID.class));
    }

    public Role extractRole(String token) {
        return this.extractClaims(token, claims -> claims.get("role", Role.class));
    }

    public UUID extractPrefectureId(String token) {
        return this.extractClaims(token, claims -> claims.get("prefectureId", UUID.class));
    }

    public boolean isValidToken(String token) {
        Date expiration = extractClaims(token, Claims::getExpiration);
        return (expiration.after(new Date(System.currentTimeMillis())));
    }


    public String extractEmail(String token) {
        return this.extractClaims(token, claims -> claims.get("email", String.class));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return  claimsTFunction.apply(extractAllClaims(token));
    }
}
