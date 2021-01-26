package com.atguigu.gmall.item.service;

import java.util.Map;

public interface ItemApiService {
    Map<String, Object> getItem(Long skuId);
}
