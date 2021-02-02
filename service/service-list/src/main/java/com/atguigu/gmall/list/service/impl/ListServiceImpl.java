package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.BaseCategoryView;
import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.list.Goods;
import com.atguigu.gmall.list.SearchAttr;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> jsonObjects = productFeignClient.getBaseCategoryList();
        return jsonObjects;
    }

    //上架同步es
    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        //获取sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //获取品牌信息
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        //获取商品分类信息
        BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
        //获取平台属性集合对象
        List<SearchAttr> searchAttrList = productFeignClient.getSearchAttrList(skuId);
        //封装Goods结果集
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        BeanUtils.copyProperties(baseCategoryView,goods);
        goods.setHotScore(0L);
        goods.setAttrs(searchAttrList);
        //将Goods添加到es索引中
        System.out.println(goods);


    }

    //下架同步es
    @Override
    public void cancelSale(Long skuId) {
        //根据skuId删除es中Goods索引的数据

    }
}
