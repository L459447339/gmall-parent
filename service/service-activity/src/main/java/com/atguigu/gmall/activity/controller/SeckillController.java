package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/activity/seckill")
public class SeckillController {

    @Autowired
    ActivityService activityService;

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
}
