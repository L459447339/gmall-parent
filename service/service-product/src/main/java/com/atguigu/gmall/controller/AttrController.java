package com.atguigu.gmall.controller;

import com.atguigu.common.result.Result;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.service.AttrService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "平台属性接口")
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class AttrController {

    @Autowired
    private AttrService service;

    //获取分类id平台属性
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") Long category1Id,
                               @PathVariable("category2Id") Long category2Id,
                               @PathVariable("category3Id") Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = service.attrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);
    }

    //添加或修改平台属性
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        boolean flag = service.saveAttrInfo(baseAttrInfo);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //根据id查询平台属性
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId) {
        List<BaseAttrValue> baseAttrValueList = service.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }

    //获得品牌分页的列表
    @GetMapping("baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit) {
        IPage<BaseTrademark> iPage = service.baseTrademark(page, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("records", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("size", iPage.getSize());
        map.put("current", iPage.getCurrent());
        map.put("pages", iPage.getPages());
        return Result.ok(map);
    }
}
