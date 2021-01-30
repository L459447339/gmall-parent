package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients("com.atguigu.gmall")
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class ListMain9203 {
    public static void main(String[] args) {
        SpringApplication.run(ListMain9203.class,args);
    }
}
