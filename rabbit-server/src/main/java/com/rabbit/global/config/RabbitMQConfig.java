package com.rabbit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String DELAY_QUEUE = "auction.delay.queue";
    public static final String DEAD_QUEUE = "auction.dead.queue";
    public static final String DEAD_EXCHANGE = "auction.dead.exchange";
    public static final String ROUTING_KEY = "auction.dead";

    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", ROUTING_KEY);
        args.put("x-queue-type", "classic");

        return new Queue(DELAY_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_QUEUE);
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE);
    }

    @Bean
    public Binding deadQueueBinding() {
        return BindingBuilder
                .bind(deadQueue())
                .to(deadExchange())
                .with(ROUTING_KEY);
    }
}
