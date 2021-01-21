package com.atguigu.gmall.controller;

import com.atguigu.common.result.Result;
import com.atguigu.gmall.product.*;
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

    //根据二级分类id查询所有三级分类
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long category2Id){
        List<BaseCategory3> baseCategory3List = service.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }
    
    //获取分类id平台属性
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") Long category1Id,
                               @PathVariable("category2Id") Long category2Id,
                               @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList = service.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfoList);
    }

    //添加平台属性
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        boolean flag = service.saveAttrInfo(baseAttrInfo);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    
    //根据id查询平台属性
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> baseAttrValueList = service.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }

    //修改平台属性
    @PutMapping("saveAttrInfo")
    public Result updateAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        boolean flag = service.updateAttrInfo(baseAttrInfo);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }

    }

}
