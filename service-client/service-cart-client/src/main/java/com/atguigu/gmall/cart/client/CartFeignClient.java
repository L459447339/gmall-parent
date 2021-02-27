package com.atguigu.gmall.cart.client;

import com.atguigu.gmall.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-cart")
public interface CartFeignClient {

    @RequestMapping("api/cart/addCart/{skuId}/{skuNum}")
    void addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum);

    @RequestMapping("api/cart/inner/cartListInner/{userId}")
    List<CartInfo> cartListInner(@PathVariable("userId") String userId);

    @RequestMapping("api/cart/inner/mergeCart/{userId}/{userTempId}")
    void mergeCart(@PathVariable("userId") String userId,@PathVariable("userTempId") String userTempId);

    @RequestMapping("api/cart/inner/deleteCartOrder/{userId}")
    void deleteCartOrder(@PathVariable("userId") Long userId);
}
