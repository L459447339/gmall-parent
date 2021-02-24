package com.atguigu.gmall.order.client;

import com.atguigu.gmall.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-order")
public interface OrderFeignClient {

    @RequestMapping("api/order/auth/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId);

    @RequestMapping("api/order/auth/getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") Long orderId);
}
