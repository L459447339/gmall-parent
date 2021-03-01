package com.atguigu.gmall.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ActivityMain8200 {
    public static void main(String[] args) {
        SpringApplication.run(ActivityMain8200.class,args);
    }
}
