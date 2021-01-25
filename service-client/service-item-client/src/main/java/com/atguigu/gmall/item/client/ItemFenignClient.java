package com.atguigu.gmall.item.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("service-product")
public interface ItemFenignClient {

    @RequestMapping("")
    Map<String, Object> index(Long skuId);
}
