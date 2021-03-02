package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.activity.OrderRecode;
import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.order.OrderInfo;

import java.util.List;

public interface ActivityService {
    List<SeckillGoods> getSeckillGoodsList();

    void seckillGoodsPut();

    SeckillGoods getSeckillGoods(Long skuId);

    void seckillOrder(String userId, Long skuId);

    void reciverSeckillGoods(String mapToJsonString);

    boolean getRedisLock(String userId);

    OrderRecode checkPlaceAnOrder(String userId);

    OrderRecode checkOrderRecode(String userId);

    String submitOrder(OrderInfo order);
}
