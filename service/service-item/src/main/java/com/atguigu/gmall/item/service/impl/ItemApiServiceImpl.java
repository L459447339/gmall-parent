package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.BaseCategoryView;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.item.service.ItemApiService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemApiServiceImpl implements ItemApiService {

    //远程调用product微服务
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String,Object> map = new HashMap<>();
        //查询sku信息和sku图片信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        map.put("skuInfo",skuInfo);
        //查询分类信息
        BaseCategoryView categoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
        map.put("categoryView",categoryView);
        //查询价格信息
        BigDecimal price = productFeignClient.getPrice(skuId);
        map.put("price",price);
        //查询销售属性
        List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId,skuInfo.getSpuId());
        map.put("spuSaleAttrList",spuSaleAttrList);
        //销售属性组合对应的sku_id
        Map<String,Object> newMap = new  HashMap<>();
        List<Map<String,Object>> maps = productFeignClient.getValuesSkuJson(skuInfo.getSpuId());
        for (Map<String, Object> stringObjectMap : maps) {
            newMap.put((String) stringObjectMap.get("group_coucat"),stringObjectMap.get("sku_id"));
        }
        map.put("valuesSkuJson", JSON.toJSONString(maps));
        return map;
    }
}
