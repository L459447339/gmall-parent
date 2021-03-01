package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.OrderInfo;

import java.util.Map;

public interface OrderService {
    String submitOrder(OrderInfo order);

    String genTradeNo(String userId);

    boolean checkTradeNo(String tradeNo, String userId);

    OrderInfo getOrderById(Long orderId);

    void updateStatus(String msg);

    void updateWareStatus(Long orderId, String status);

    Map<String, Object> getOrderList(Long page, Long limit);
}
