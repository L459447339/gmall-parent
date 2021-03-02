package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        boolean flag = orderService.checkTradeNo(tradeNo,userId);
        //如果检查一致才提交订单
        if(flag){
            order.setUserId(Long.parseLong(userId));
            String orderId = orderService.submitOrder(order);
            if(!StringUtils.isEmpty(orderId)){
                return Result.ok(orderId);
            }else {
                return Result.fail().message("请重新提交订单");
            }
        }else {
            return Result.fail().message("提交错误");
        }
    }

    //生成tradeNo防止重复提交订单
    @RequestMapping("auth/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId){
        String tradeNo = orderService.genTradeNo(userId);
        return tradeNo;
    }

    //根据orderId查询订单信息
    @RequestMapping("auth/getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") Long orderId){
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        return orderInfo;
    }

    //查询order列表
    @RequestMapping("auth/{page}/{limit}")
    public Result getOrderList(@PathVariable("page") Long page,@PathVariable("limit") Long limit){
        Map<String,Object> map = orderService.getOrderList(page,limit);
        return Result.ok(map);
    }

    //生成秒杀商品订单
    @RequestMapping("auth/submitOrderSeckill")
    String submitOrderSeckill(@RequestBody OrderInfo order){
        String orderId = orderService.submitOrder(order);
        return orderId;
    }

}
