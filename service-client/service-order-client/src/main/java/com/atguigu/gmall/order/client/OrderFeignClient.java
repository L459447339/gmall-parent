package com.atguigu.gmall.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-order")
public interface OrderFeignClient {

    @RequestMapping("api/order/auth/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId);
}
