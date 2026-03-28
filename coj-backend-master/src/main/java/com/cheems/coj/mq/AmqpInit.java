package com.cheems.coj.mq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AmqpInit implements CommandLineRunner {

    @Resource
    public AmqpAdmin amqpAdmin;


    @Override
    public void run(String... args) throws Exception {
        amqpAdmin.initialize();
    }
}
