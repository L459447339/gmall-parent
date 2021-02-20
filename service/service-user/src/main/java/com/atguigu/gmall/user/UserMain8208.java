package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
public class UserMain8208 {
    public static void main(String[] args) {
        SpringApplication.run(UserMain8208.class,args);
    }
}
