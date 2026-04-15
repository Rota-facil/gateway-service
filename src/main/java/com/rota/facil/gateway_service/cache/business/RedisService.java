package com.rota.facil.gateway_service.cache.business;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void putInvalidTokenInCache(UUID keyUserId, String token) {
        redisTemplate.opsForValue().set(keyUserId.toString(), token);
    }

    public String getInvalidTokenOfCache(UUID keyUserId) {
        Object token =  redisTemplate.opsForValue().get(keyUserId.toString());
        return (token != null) ? token.toString() : null;
    }
}
