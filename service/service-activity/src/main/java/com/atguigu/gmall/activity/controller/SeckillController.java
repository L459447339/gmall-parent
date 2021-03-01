package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/activity/seckill")
public class SeckillController {

    @Autowired
    ActivityService activityService;

    //查询秒杀商品列表
    @RequestMapping("getSeckillGoodsList")
    List<SeckillGoods> getSeckillGoodsList(){
        List<SeckillGoods> seckillGoodsList = activityService.getSeckillGoodsList();
        return seckillGoodsList;
    }

    //秒杀商品入库
    @RequestMapping("seckillGoodsPut")
    Result seckillGoodsPut(){
        activityService.seckillGoodsPut();
        return Result.ok();
    }

    //获取秒杀商品状态
    @RequestMapping("getGoodsStatus/{skuId}")
    public Result getGoodsStatus(@PathVariable("skuId") Long skuId){
        String status = (String) CacheHelper.get(skuId + "");
        return Result.ok(status);
    }

    @RequestMapping("getSeckillGoods/{skuId}")
    SeckillGoods getSeckillGoods(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods = activityService.getSeckillGoods(skuId);
        return seckillGoods;
    }
}
