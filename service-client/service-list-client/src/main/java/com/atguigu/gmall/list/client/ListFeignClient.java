package com.atguigu.gmall.list.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.list.SearchParam;
import com.atguigu.gmall.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-list")
public interface ListFeignClient {

    @RequestMapping("api/list/index")
    List<JSONObject> getBaseCategoryList();

    @RequestMapping("api/list/onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/list")
    SearchResponseVo list(@RequestBody SearchParam searchParam);
}
