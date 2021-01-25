package com.atguigu.gmall.controller;

import com.atguigu.common.result.Result;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "Sku接口")
@RestController
@CrossOrigin
@RequestMapping("admin/product")
public class SkuController {

    @Autowired
    private SkuService service;

    //获取Spu图片列表
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImage> spuImages = service.spuImageList(spuId);
        return Result.ok(spuImages);
    }

    //根据SpuId获取销售属性
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId) {
        List<SpuSaleAttr> spuSaleAttrs = service.spuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrs);
    }

    //添加sku
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        service.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //获取sku分页列表
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit) {
        IPage<SkuInfo> iPage = service.list(page, limit);
        Map<String, Object> map = new HashMap<>();
        map.put("records", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("size", iPage.getSize());
        map.put("current", iPage.getCurrent());
        map.put("pages", iPage.getPages());
        return Result.ok(map);
    }

    //上架
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        service.onSale(skuId);
        return Result.ok();
    }

    //下架
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        service.cancelSale(skuId);
        return Result.ok();
    }
}
