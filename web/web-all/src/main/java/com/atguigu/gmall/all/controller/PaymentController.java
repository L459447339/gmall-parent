package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PaymentController {

    @Autowired
    OrderFeignClient orderFeignClient;

    //跳转支付页面，需要返回orderInfo对象
    @RequestMapping("pay.html")
    public String pay(Long orderId, Model model){
        OrderInfo orderInfo = orderFeignClient.getOrderById(orderId);
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay.html";
    }
}
