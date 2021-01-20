package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.dao.BaseCategory1Mapper;
import com.atguigu.gmall.dao.BaseCategory2Mapper;
import com.atguigu.gmall.product.BaseCategory1;
import com.atguigu.gmall.product.BaseCategory2;
import com.atguigu.gmall.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

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
}
