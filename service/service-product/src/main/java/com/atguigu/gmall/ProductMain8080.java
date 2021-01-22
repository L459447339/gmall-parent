package com.atguigu.gmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.atguigu.gmall.mapper")
@EnableSwagger2
public class ProductMain8080 {
    public static void main(String[] args) {
        SpringApplication.run(ProductMain8080.class,args);
    }
}
