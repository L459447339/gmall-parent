package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart")
@CrossOrigin
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

    //查询购物车列表
    @RequestMapping("cartList")
    public Result cartList(){
        //模拟用户id
        String userId = "12";
        List<CartInfo> cartInfoList = cartService.cartList(userId);
        return Result.ok(cartInfoList);
    }

    //修改购物车选中状态
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result ischeckCart(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer isChecked){
        //模拟用户id
        String userId = "12";
        cartService.ischeckCart(skuId,isChecked,userId);
        return Result.ok();
    }

    //删除购物车商品
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId){
        //模拟用户id
        String userId = "12";
        cartService.deleteCart(skuId,userId);
        return Result.ok();
    }
}
