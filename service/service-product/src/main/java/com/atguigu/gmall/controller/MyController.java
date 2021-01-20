package com.atguigu.gmall.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MyController {

    @RequestMapping("test")
    public String test(){
        return "hello,world";
    }

}
