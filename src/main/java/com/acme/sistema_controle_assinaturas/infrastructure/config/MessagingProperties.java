package com.acme.sistema_controle_assinaturas.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(
        String exchange,
        String queue,
        String routingKey,
        @DefaultValue("false") boolean mock
) {
}
