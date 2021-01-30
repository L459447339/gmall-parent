package com.atguigu.gmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.aspect.GmallCache;
import com.atguigu.gmall.bean.BaseCategoryView;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.CategoryService;
import com.atguigu.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.Cacheable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private SkuService skuService;

    @Autowired
    private CategoryService categoryService;



    @GetMapping("getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        try {
            return skuService.getSkuInfo(skuId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("getBaseCategoryView/{categoryId3}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("categoryId3") Long categoryId3){
        BaseCategoryView categoryView = categoryService.getBaseCategoryView(categoryId3);
        return categoryView;
    }

    @GetMapping("getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuService.getPrice(skuId);
        return price;
    }

    @GetMapping("getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrList = skuService.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrList;
    }

    @GetMapping("getValuesSkuJson/{spuId}")
    List<Map<String, Object>> getValuesSkuJson(@PathVariable("spuId") Long spuId){
        return skuService.getValuesSkuJson(spuId);
    }

    @GetMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList(){
        List<JSONObject> categoryList = categoryService.getBaseCategoryList();
        return categoryList;
    }
}
