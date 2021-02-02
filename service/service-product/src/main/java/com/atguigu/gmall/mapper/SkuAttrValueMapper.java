package com.atguigu.gmall.mapper;

import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.list.SearchAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
    List<SearchAttr> seleteSearchAttrList(Long skuId);
}
