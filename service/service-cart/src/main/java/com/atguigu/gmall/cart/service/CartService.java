package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.CartInfo;

import java.util.List;

public interface CartService {

    void addCart(Long skuId,Integer skuNum,String userId,String userTempId);

    List<CartInfo> cartList(String userId,String userTempId);

    void ischeckCart(Long skuId, Integer isChecked,String userId,String userTempId);

    void deleteCart(Long skuId, String userId,String userTempId);

    List<CartInfo> cartListInner(String userId);
}
