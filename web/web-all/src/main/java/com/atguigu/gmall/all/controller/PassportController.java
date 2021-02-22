package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PassportController {

    @RequestMapping("login")
    public String login(String ReturnUrl, Model model){
//        System.out.println(ReturnUrl);
        model.addAttribute("originUrl",ReturnUrl);
        return "login";
    }

}
