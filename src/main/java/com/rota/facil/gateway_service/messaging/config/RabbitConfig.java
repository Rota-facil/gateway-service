package com.rota.facil.gateway_service.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${rabbitmq.auth.exchange}")
    private String authExchange;

    @Value("${rabbitmq.gateway.user.deleted.queue}")
    private String userDeletedQueue;

    @Value("${rabbitmq.gateway.user.email.changed.queue}")
    private String userEmailChangedQueue;

    @Value("${rabbitmq.user.deleted.routing.key}")
    private String userDeletedRoutingKey;

    @Value("${rabbitmq.user.email.changed.routing.key}")
    private String userEmailChangedRoutingKey;


    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setMessageConverter(messageConverter);
        return simpleRabbitListenerContainerFactory;
    }


    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(authExchange);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(userDeletedQueue);
    }

    @Bean
    public Queue userEmailChangedQueue() {
        return new Queue(userEmailChangedQueue);
    }


    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder.bind(this.userDeletedQueue()).to(this.authExchange()).with(userDeletedRoutingKey);
    }

    @Bean
    public Binding userEmailChangedBinding() {
        return BindingBuilder.bind(this.userEmailChangedQueue()).to(this.authExchange()).with(userEmailChangedRoutingKey);
    }
}
