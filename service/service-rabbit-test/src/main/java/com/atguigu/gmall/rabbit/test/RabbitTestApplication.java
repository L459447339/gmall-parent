package com.atguigu.gmall.rabbit.test;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall",exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class RabbitTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitTestApplication.class,args);
    }
}
