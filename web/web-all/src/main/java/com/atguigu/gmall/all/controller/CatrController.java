package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CatrController {

    @Autowired
    CartFeignClient cartFeignClient;

    //添加或修改购物车
    @RequestMapping("addCart.html")
    public String addCart(Long skuId,Integer skuNum){
        //调用cart模块进行处理购物车业务
        cartFeignClient.addCart(skuId,skuNum);
        return "cart/addCart";
    }
}
