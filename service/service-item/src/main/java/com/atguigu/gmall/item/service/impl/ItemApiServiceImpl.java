package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemApiService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemApiServiceImpl implements ItemApiService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String,Object> map = productFeignClient.getItem(skuId);
        return map;
    }
}
