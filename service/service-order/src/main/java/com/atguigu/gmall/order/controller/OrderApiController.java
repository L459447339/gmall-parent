package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    OrderService orderService;

    //提交订单，传入tradeNo标识号和order对象，返回订单id
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(String tradeNo, @RequestBody OrderInfo order, HttpServletRequest request){
        System.out.println("访问到了submitOrder...");
        String userId = request.getHeader("userId");
        order.setUserId(Long.parseLong(userId));
        String orderId = orderService.submitOrder(tradeNo,order);
        return Result.ok(orderId);
    }
}
