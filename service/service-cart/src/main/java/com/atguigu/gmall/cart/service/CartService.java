package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.CartInfo;

import java.util.List;

public interface CartService {

    void addCart(CartInfo cartInfo);

    List<CartInfo> cartList(String userId);

    void ischeckCart(Long skuId, Integer isChecked,String userId);

    void deleteCart(Long skuId, String userId);
}
