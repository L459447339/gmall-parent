package com.atguigu.gmall.service;

import com.atguigu.gmall.product.*;

import java.util.List;

public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    boolean saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    boolean updateAttrInfo(BaseAttrInfo baseAttrInfo);
}
