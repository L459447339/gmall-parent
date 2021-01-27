package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {
    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);


    SkuInfo getSkuInfo(Long skuId) throws InterruptedException;

    BigDecimal getPrice(Long skuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId,Long spuId);

    List<Map<String, Object>> getValuesSkuJson(Long skuId);
}
