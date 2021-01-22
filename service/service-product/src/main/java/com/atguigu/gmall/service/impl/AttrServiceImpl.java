package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


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
                    List<BaseAttrValue> attrValueList = new ArrayList<>();
                    for (BaseAttrValue baseAttrValue : baseAttrValueList) {
                        attrValueList.add(baseAttrValue);
                    }
                    attrInfo.setAttrValueList(attrValueList);
                }
                return attrInfoList;
            }
        }
        return null;
    }

    //添加或修改平台属性
    @Override
    public boolean saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long id = baseAttrInfo.getId();
        if(id!=null && id>-1) {
            //修改方法
            QueryWrapper<BaseAttrValue> attrValueWrapper = new QueryWrapper<>();
            attrValueWrapper.eq("attr_id",id);
            baseAttrValueMapper.delete(attrValueWrapper);
        }
        else {
            //添加方法
            baseAttrInfoMapper.insert(baseAttrInfo);
            id=baseAttrInfo.getId();
        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if(attrValueList!=null && attrValueList.size()>0){
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(id);
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
        return true;
    }

    //根据id查询平台属性
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id",attrId);
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.selectList(queryWrapper);
        return attrValueList;
    }
}
