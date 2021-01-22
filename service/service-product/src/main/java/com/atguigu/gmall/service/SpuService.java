package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;


public interface SpuService {
    IPage<SpuInfo> getSpuInfo(Long page, Long limit, Long category3Id);
}
