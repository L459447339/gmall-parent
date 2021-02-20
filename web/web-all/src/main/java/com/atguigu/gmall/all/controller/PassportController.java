package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PassportController {

    @RequestMapping("login")
    public String login(String ReturnUrl){
        return "login";
    }

}
