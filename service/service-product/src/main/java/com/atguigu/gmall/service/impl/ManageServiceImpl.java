package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.dao.*;
import com.atguigu.gmall.product.*;
import com.atguigu.gmall.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    //查询所有一级分类
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(null);
        return baseCategory1List;
    }

    //根据一级分类id查询所有二级分类
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("category1_id",category1Id);
        return baseCategory2Mapper.selectList(queryWrapper);
    }

    //根据二级分类id查询所有三级分类
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",category2Id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }

    //获取分类id的平台属性
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        if(category1Id!=0 && category2Id==0 && category3Id==0){
            QueryWrapper<BaseAttrInfo> categoryWrapper1 = new QueryWrapper<>();
            categoryWrapper1.eq("category_level",1);
            categoryWrapper1.eq("category_id",category1Id);
            List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectList(categoryWrapper1);
            if(attrInfoList!=null && attrInfoList.size()>0){
                for (BaseAttrInfo attrInfo : attrInfoList) {
                    Long id = attrInfo.getId();
                    QueryWrapper<BaseAttrValue> attrValueWrapper = new QueryWrapper<>();
                    attrValueWrapper.eq("attr_id",id);
                    List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(attrValueWrapper);
                    List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : baseAttrValueList) {
                        attrValueList.add(baseAttrValue);
                    }
                }
                return attrInfoList;
            }
        }

        if(category2Id!=0 && category1Id!=0 && category3Id==0){
            QueryWrapper<BaseAttrInfo> categoryWrapper2 = new QueryWrapper<>();
            categoryWrapper2.eq("category_level",2);
            categoryWrapper2.eq("category_id",category2Id);
            List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectList(categoryWrapper2);
            if(attrInfoList!=null && attrInfoList.size()>0){
                for (BaseAttrInfo attrInfo : attrInfoList) {
                    Long id = attrInfo.getId();
                    QueryWrapper<BaseAttrValue> baseAttrValueWrapper = new QueryWrapper<>();
                    baseAttrValueWrapper.eq("attr_id",id);
                    List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(baseAttrValueWrapper);
                    List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : baseAttrValueList) {
                        attrValueList.add(baseAttrValue);
                    }
                }
                return attrInfoList;
            }
        }

        if(category3Id!=0 && category2Id!=0 && category1Id!=0){
            QueryWrapper<BaseAttrInfo> categoryWrapper3 = new QueryWrapper<>();
            categoryWrapper3.eq("category_level",3);
            categoryWrapper3.eq("category_id",category3Id);
            List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectList(categoryWrapper3);
            if(attrInfoList!=null && attrInfoList.size()>0){
                for (BaseAttrInfo attrInfo : attrInfoList) {
                    Long id = attrInfo.getId();
                    QueryWrapper<BaseAttrValue> baseAttrValueWrapper = new QueryWrapper<>();
                    baseAttrValueWrapper.eq("attr_id",id);
                    List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(baseAttrValueWrapper);
                    List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
                    attrInfo.setAttrValueList(baseAttrValueList);
//                    for (BaseAttrValue baseAttrValue : baseAttrValueList) {
//                        attrValueList.add(baseAttrValue);
//                    }
                }
                return attrInfoList;
            }
        }
      return null;
    }

    @Override
    public boolean saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        return false;
    }
}
