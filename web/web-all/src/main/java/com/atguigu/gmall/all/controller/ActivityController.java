package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.activity.client.ActivityFeignClient;
import com.atguigu.gmall.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    ActivityFeignClient activityFeignClient;

    @RequestMapping("seckill.html")
    public String seckillIndex(Model model){
        List<SeckillGoods> seckillGoodsList = activityFeignClient.getSeckillGoodsList();
        model.addAttribute("list",seckillGoodsList);
        return "seckill/index";
    }

    @RequestMapping("seckill/{skuId}.html")
    public String seckillItem(@PathVariable("skuId") Long skuId,Model model){
        SeckillGoods seckillGoods = activityFeignClient.getSeckillGoods(skuId);
        model.addAttribute("item",seckillGoods);
        return "seckill/item";
    }
}
