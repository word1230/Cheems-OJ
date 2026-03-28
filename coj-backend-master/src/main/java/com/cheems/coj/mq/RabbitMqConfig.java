package com.cheems.coj.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {


    @Bean
    public DirectExchange judgeExchange() {
        return new DirectExchange(MqConstants.JUDGE_EXCHANGE, true, false);
    }

    @Bean
    public Queue judgeQueue() {
        return QueueBuilder.durable(MqConstants.JUDGE_QUEUE)
                .withArgument("x-dead-letter-exchange", MqConstants.JUDGE_DLX)
                .withArgument("x-dead-letter-routing-key", MqConstants.JUDGE_DLQ_ROUTING_KEY)
                .build();
    }


    @Bean
    public Binding judgeBinding(@Qualifier("judgeQueue") Queue judgeQueue, @Qualifier("judgeExchange") DirectExchange judgeExchange) {
        return BindingBuilder.bind(judgeQueue).to(judgeExchange).with(MqConstants.JUDGE_ROUTING_KEY);
    }


    @Bean
    public DirectExchange judgeDlxExchange() {
        return new DirectExchange(MqConstants.JUDGE_DLX, true, false);
    }

    @Bean
    public Queue judgeDlxQueue() {
        return QueueBuilder.durable(MqConstants.JUDGE_DLQ).build();
    }


    @Bean
    public Binding judgeDlxBinding(@Qualifier("judgeDlxQueue") Queue judgeDlxQueue, @Qualifier("judgeDlxExchange") DirectExchange judgeDlxExchange) {
        return BindingBuilder.bind(judgeDlxQueue).to(judgeDlxExchange).with(MqConstants.JUDGE_DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
