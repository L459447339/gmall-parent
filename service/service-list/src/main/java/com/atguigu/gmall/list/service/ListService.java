package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.SearchParam;
import com.atguigu.gmall.list.SearchResponseVo;

import java.util.List;

public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void incrHotScore(Long skuId);

    SearchResponseVo list(SearchParam searchParam);
}
