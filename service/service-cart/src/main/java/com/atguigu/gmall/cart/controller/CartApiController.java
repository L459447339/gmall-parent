package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
//@CrossOrigin
public class CartApiController {

    @Autowired
    CartService cartService;

    //添加或修改购物车信息
    @RequestMapping("addCart/{skuId}/{skuNum}")
    void addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum, HttpServletRequest request){
        //从请求头中获取userId和userTempId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        cartService.addCart(skuId,skuNum,userId,userTempId);
    }

    //查询购物车列表
    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request){
        //从请求头中获取userId和userTempId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        List<CartInfo> cartInfoList = cartService.cartList(userId,userTempId);
        return Result.ok(cartInfoList);
    }

    //修改购物车选中状态
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result ischeckCart(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer isChecked,HttpServletRequest request){
        //从请求头中获取userId和userTempId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        cartService.ischeckCart(skuId,isChecked,userId,userTempId);
        return Result.ok();
    }

    //删除购物车商品
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,HttpServletRequest request){
        //从请求头中获取userId和userTempId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        cartService.deleteCart(skuId,userId,userTempId);
        return Result.ok();
    }

    //获取购物车选中状态下的商品
    @RequestMapping("inner/cartListInner/{userId}")
    List<CartInfo> cartListInner(@PathVariable("userId") String userId){
        List<CartInfo> cartInfoList = cartService.cartListInner(userId);
        return cartInfoList;
    }

    //合并临时购物车数据
    @RequestMapping("inner/mergeCart/{userId}/{userTempId}")
    void mergeCart(@PathVariable("userId") String userId,@PathVariable("userTempId") String userTempId){
        cartService.mergeCart(userId,userTempId);
    }

}
