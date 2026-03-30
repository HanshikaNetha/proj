package com.example.collaborationService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    public static final String NOTIFICATION_EXCHANGE="notification.exchange";
    public static final String NOTIFICATION_QUEUE="notification.queue";
    public static final String ROUTING_KEY="notification.routing";

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    @Bean
    public Queue notificationQueue(){
        return new Queue(NOTIFICATION_QUEUE);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(notificationQueue()).to(directExchange()).with(ROUTING_KEY);
    }
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
