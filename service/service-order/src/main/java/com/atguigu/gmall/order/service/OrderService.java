package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.OrderInfo;

public interface OrderService {
    String submitOrder(String tradeNo, OrderInfo order);
}
