package com.rota.facil.gateway_service.messaging.dto.receive;

import java.util.UUID;

public record AuthUserUpdatedEventReceive(
        UUID userId,
        String userToken
) {
}
