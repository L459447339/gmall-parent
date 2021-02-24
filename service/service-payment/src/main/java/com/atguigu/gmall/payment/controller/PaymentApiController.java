package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
public class PaymentApiController {

    @Autowired
    PaymentService paymentService;

    @RequestMapping("alipay/submit/{orderId}")
    public String submit(@PathVariable("orderId") Long orderId){
        //调用支付宝接口
        String from = paymentService.tradePagePay(orderId);
        return from;
    }
}
