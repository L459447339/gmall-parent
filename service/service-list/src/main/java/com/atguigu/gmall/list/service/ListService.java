package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void incrHotScore(Long skuId);
}
