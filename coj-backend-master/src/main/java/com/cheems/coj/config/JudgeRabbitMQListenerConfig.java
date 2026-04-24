package com.cheems.coj.config;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cheems
 * @description 判题队列配置, 限制消费者线程数量和每个消费者一次获取的消息数
 */
@Configuration
public class JudgeRabbitMQListenerConfig {

    @Value("${judge.rabbitmq.concurrent-consumer:1}")
    private int concurrentConsumers;

    @Value("${judge.rabbitmq.max-concurrent-consumer:2}")
    private int maxConcurrentConsumers;

    @Value("${judge.rabbitmq.prefetch-count:2}")
    private int prefetchCount;

    @Bean("judgeListenerContainersFactory")
    public SimpleRabbitListenerContainerFactory judgeListenerContainersFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setConcurrentConsumers(concurrentConsumers);
        simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        simpleRabbitListenerContainerFactory.setPrefetchCount(prefetchCount);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleRabbitListenerContainerFactory;
    }

}
