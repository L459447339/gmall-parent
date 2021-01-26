package com.atguigu.gmall.controller;

import com.atguigu.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private SkuService service;

    @GetMapping("getItem/{skuId}")
    public Map<String,Object> getItem(@PathVariable("skuId") Long skuId){
        Map<String,Object> map = new HashMap<>();
        map = service.getItem(skuId);
        return map;
    }

}
