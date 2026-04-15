package com.rota.facil.gateway_service.messaging.consumers;

import com.rota.facil.gateway_service.cache.business.RedisService;
import com.rota.facil.gateway_service.messaging.dto.receive.AuthUserDeletedEventReceive;
import com.rota.facil.gateway_service.messaging.dto.receive.AuthUserUpdatedEventReceive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitAuthEventConsumer {
    private final RedisService redisService;

    @RabbitListener(queues = "${rabbitmq.gateway.user.deleted.queue}")
    public void handlerUserDeleted(AuthUserDeletedEventReceive authUserDeletedEventReceive) {
        try {
            String token = authUserDeletedEventReceive.userToken();
            UUID userId = authUserDeletedEventReceive.userId();
            redisService.putInvalidTokenInCache(token, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${rabbitmq.gateway.user.updated.queue}")
    public void handlerUserUpdated(AuthUserUpdatedEventReceive authUserUpdatedEventReceive) {
        try {
            String token = authUserUpdatedEventReceive.userToken();
            UUID userId = authUserUpdatedEventReceive.userId();
            redisService.putInvalidTokenInCache(token, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

