package com.cheems.cojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.cheems.cojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.cheems")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.cheems.cojbackendserviceclient.service"})
public class cojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(cojBackendUserServiceApplication.class, args);
    }

}
