package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.bean.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface AttrService {

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    boolean saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    IPage<BaseTrademark> baseTrademark(Long page, Long limit);
}
