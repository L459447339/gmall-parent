package com.atguigu.gmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableFeignClients("com.atguigu.gmall")
public class OrderMain8204 {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain8204.class,args);
    }
}
