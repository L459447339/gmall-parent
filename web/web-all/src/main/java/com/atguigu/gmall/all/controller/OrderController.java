package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

    @Autowired
    UserFeignClient userFeignClient;

    @RequestMapping("myOrder.html")
    public String myOrder(HttpServletRequest request){
        //获取userId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        System.out.println(userId);
        System.out.println(userTempId);
//        userFeignClient.trade();
        return "order/myOrder";
    }

}
