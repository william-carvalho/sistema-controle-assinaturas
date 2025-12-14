package com.acme.sistema_controle_assinaturas.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingConfig {

    @Bean
    @ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
    public Queue subscriptionQueue(MessagingProperties properties) {
        return QueueBuilder.durable(properties.queue()).build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
    public DirectExchange subscriptionExchange(MessagingProperties properties) {
        return new DirectExchange(properties.exchange());
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
    public Binding subscriptionBinding(Queue subscriptionQueue, DirectExchange subscriptionExchange, MessagingProperties properties) {
        return BindingBuilder.bind(subscriptionQueue).to(subscriptionExchange).with(properties.routingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
