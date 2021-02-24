package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.config.AlipayClientConfig;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("api/payment")
public class PaymentApiController {

    @Autowired
    PaymentService paymentService;

    //生成支付宝二维码
    @ResponseBody
    @RequestMapping("alipay/submit/{orderId}")
    public String submit(@PathVariable("orderId") Long orderId){
        //调用支付宝接口
        String from = paymentService.tradePagePay(orderId);
        return from;
    }

    //扫码支付后的回调
    @RequestMapping("alipay/callback/return")
    public String callback(String trade_no, String out_trade_no, HttpServletRequest request){
        //验证是否支付成功
        boolean flag = paymentService.checkPaymentStatus(out_trade_no);
        if(flag){
            String queryString = request.getQueryString();
            //修改支付信息
            paymentService.updatePayment(trade_no,out_trade_no,queryString);
            return "redirect:" + AlipayClientConfig.returnOrderUrl;
        }else {
            return "redirect:http://cart.gmall.com/cart/cart.html" ;
        }
    }
}
