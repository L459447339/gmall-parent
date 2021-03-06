package com.atguigu.gmall.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "Spu接口")
@RestController
@RequestMapping("admin/product")
//@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    //获取spu分页列表
    @GetMapping("{page}/{limit}")
    public Result getSpuInfo(@PathVariable("page") Long page,
                             @PathVariable("limit") Long limit,
                             @RequestParam("category3Id") Long category3Id) {
        IPage<SpuInfo> iPage = spuService.getSpuInfo(page, limit, category3Id);
        Map<String, Object> map = new HashMap<>();
        map.put("records", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("size", iPage.getSize());
        map.put("current", iPage.getCurrent());
        map.put("pages", iPage.getPages());
        return Result.ok(map);
    }

    //获取销售属性
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList() {
        List<BaseSaleAttr> spuSaleAttrs = spuService.baseSaleAttrList();
        return Result.ok(spuSaleAttrs);
    }

    //获取品牌属性
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList() {
        List<BaseTrademark> baseTrademarks = spuService.getTrademarkList();
        return Result.ok(baseTrademarks);
    }

    //添加spu
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }


}
