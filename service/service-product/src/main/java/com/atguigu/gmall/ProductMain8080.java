package com.atguigu.gmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall")
@MapperScan("com.atguigu.gmall.mapper")
@EnableSwagger2
@EnableDiscoveryClient
public class ProductMain8080 {
    public static void main(String[] args) {
        SpringApplication.run(ProductMain8080.class,args);
    }
}
