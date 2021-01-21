package com.atguigu.gmall.service;

import com.atguigu.gmall.product.BaseAttrInfo;
import com.atguigu.gmall.product.BaseCategory1;
import com.atguigu.gmall.product.BaseCategory2;
import com.atguigu.gmall.product.BaseCategory3;

import java.util.List;

public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    boolean saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
