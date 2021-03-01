package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.activity.SeckillGoods;

import java.util.List;

public interface ActivityService {
    List<SeckillGoods> getSeckillGoodsList();

    void seckillGoodsPut();

    SeckillGoods getSeckillGoods(Long skuId);
}
