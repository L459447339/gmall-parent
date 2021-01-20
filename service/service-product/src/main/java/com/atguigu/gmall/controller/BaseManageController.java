package com.atguigu.gmall.controller;

import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.BaseCategory1;
import com.atguigu.gmall.product.BaseCategory2;
import com.atguigu.gmall.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("admin/product")
public class BaseManageController {

    @Autowired
    private ManageService service;

    //查询所有的一级分类
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> baseCategory1List = service.getCategory1();
        return Result.ok(baseCategory1List);
    }

    //根据一级分类id查询所有二级分类
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id){
        List<BaseCategory2> baseCategory2List = service.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

}
