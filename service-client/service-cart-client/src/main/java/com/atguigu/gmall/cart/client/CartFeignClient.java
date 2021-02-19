package com.atguigu.gmall.cart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-cart")
public interface CartFeignClient {

    @RequestMapping("api/cart/addCart/{skuId}/{skuNum}")
    void addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum);
}
