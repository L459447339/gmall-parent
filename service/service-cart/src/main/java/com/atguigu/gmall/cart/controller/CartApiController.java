package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Autowired
    CartService cartService;

    //添加或修改购物车信息
    @RequestMapping("addCart/{skuId}/{skuNum}")
    void addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum){
        //模拟用户id
        String userId = "12";
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuNum(skuNum);
        cartInfo.setUserId(userId);
        cartService.addCart(cartInfo);
    }

}
