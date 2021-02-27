package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.OrderInfo;

public interface OrderService {
    String submitOrder(OrderInfo order);

    String genTradeNo(String userId);

    boolean checkTradeNo(String tradeNo, String userId);

    OrderInfo getOrderById(Long orderId);

    void updateStatus(String msg);

    void updateWareStatus(Long orderId, String status);
}
