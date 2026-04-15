package com.rota.facil.gateway_service.cache.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void putInvalidTokenInCache(String keyToken, UUID userId) {
        redisTemplate.opsForValue().set(keyToken, userId.toString(), 86400000L, TimeUnit.MILLISECONDS);
    }

    public UUID getInvalidTokenOfCache(String token) {
        String userId = redisTemplate.opsForValue().get(token);
        return (userId != null) ? UUID.fromString(userId) : null;
    }
}
