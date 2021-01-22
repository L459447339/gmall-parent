package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;


public interface SpuService {
    IPage<SpuInfo> getSpuInfo(Long page, Long limit, Long category3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    List<BaseTrademark> getTrademarkList();

    void saveSpuInfo(SpuInfo spuInfo);
}
