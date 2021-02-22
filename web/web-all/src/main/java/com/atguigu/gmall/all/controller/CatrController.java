package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CatrController {

    //添加或修改购物车
    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("addCart.html")
    public String addCart(Long skuId, Integer skuNum) {
        //调用cart模块进行处理购物车业务
        cartFeignClient.addCart(skuId, skuNum);
        //静态传参
        return "redirect:cart/addCartSuccess.html?skuName=?&price=?";
    }

    //跳转结算购物车界面
    @RequestMapping({"cart/cart.html", "cart.html"})
    public String cartList() {
        return "cart/index";
    }
}
