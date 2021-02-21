package com.atguigu.gmall.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.CategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //查询所有的一级分类
    @GetMapping("getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> baseCategory1List = categoryService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    //根据一级分类id查询所有二级分类
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id) {
        List<BaseCategory2> baseCategory2List = categoryService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    //根据二级分类id查询所有三级分类
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long category2Id) {
        List<BaseCategory3> baseCategory3List = categoryService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }
}
