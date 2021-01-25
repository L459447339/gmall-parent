package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemControoler {

    @RequestMapping("test")
    public String test(Model model, HttpServletRequest request, HttpSession session){
        model.addAttribute("hello","hello thymeleaf");
        request.setAttribute("test",12345);
        session.setAttribute("sessionVal","hello session");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("元素"+i);
        }
        model.addAttribute("list",list);

        return "test";
    }

}
