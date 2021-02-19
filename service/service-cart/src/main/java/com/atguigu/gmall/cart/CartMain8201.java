package com.atguigu.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class CartMain8201 {
    public static void main(String[] args) {
        SpringApplication.run(CartMain8201.class,args);
    }
}
