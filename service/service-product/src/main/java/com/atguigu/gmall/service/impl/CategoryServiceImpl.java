package com.atguigu.gmall.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.aspect.GmallCache;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.mapper.*;
import com.atguigu.gmall.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;


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
        queryWrapper.eq("category1_id", category1Id);
        return baseCategory2Mapper.selectList(queryWrapper);
    }

    //根据二级分类id查询所有三级分类
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }

    //查询商品详情的分类信息
    @Override
    public BaseCategoryView getBaseCategoryView(Long categoryId3) {
        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", categoryId3);
        return baseCategoryViewMapper.selectOne(queryWrapper);
    }

    //查询首页的分类信息，并封装到JSONObject中
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> jsonObjects1 = new ArrayList<>();
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //使用stream流进行分组,分组完返回一个map对象，key是分组的属性，value是每个属性对应的多个值装在list中
        Map<Long, List<BaseCategoryView>> collect1 = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //第一层一级分类
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : collect1.entrySet()) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("categoryId", entry1.getKey());
            jsonObject1.put("categoryName", entry1.getValue().get(0).getCategory1Name());
            //第二层二级分类
            Map<Long, List<BaseCategoryView>> collect2 = entry1.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            ArrayList<JSONObject> jsonObjects2 = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : collect2.entrySet()) {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("categoryId", entry2.getKey());
                jsonObject2.put("categoryName", entry2.getValue().get(0).getCategory2Name());
                //第三次三级分类
                Map<Long, List<BaseCategoryView>> collect3 = entry2.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                ArrayList<JSONObject> jsonObjects3 = new ArrayList<>();
                for (Map.Entry<Long, List<BaseCategoryView>> entry3 : collect3.entrySet()) {
                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("categoryId", entry3.getKey());
                    jsonObject3.put("categoryName", entry3.getValue().get(0).getCategory3Name());
                    jsonObject3.put("categoryChild", null);
                    jsonObjects3.add(jsonObject3);
                }
                jsonObject2.put("categoryChild", jsonObjects3);
                jsonObjects2.add(jsonObject2);
            }
            jsonObject1.put("categoryChild", jsonObjects2);
            jsonObjects1.add(jsonObject1);
        }
        return jsonObjects1;
    }
}
