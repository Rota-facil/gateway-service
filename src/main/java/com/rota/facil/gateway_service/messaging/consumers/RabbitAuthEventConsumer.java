package com.rota.facil.gateway_service.messaging.consumers;

import com.rota.facil.gateway_service.cache.business.RedisService;
import com.rota.facil.gateway_service.messaging.dto.receive.AuthUserDeletedEventReceive;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitAuthEventConsumer {
    private final RedisService redisService;

    @RabbitListener(queues = "${rabbitmq.gateway.user.deleted.queue}")
    public void handlerUserDeleted(AuthUserDeletedEventReceive authUserDeletedEventReceive) {
        UUID userId = authUserDeletedEventReceive.userId();
        String token = authUserDeletedEventReceive.userToken();
        redisService.putInvalidTokenInCache(userId, token);
    }
}

