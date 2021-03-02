package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.OrderRecode;
import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/activity/seckill")
public class SeckillApiController {

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

    //查询秒杀商品详情
    @RequestMapping("getSeckillGoods/{skuId}")
    SeckillGoods getSeckillGoods(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods = activityService.getSeckillGoods(skuId);
        return seckillGoods;
    }

    //生成抢购码
    @RequestMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String seckillCode = MD5.encrypt(skuId + userId);
        return Result.ok(seckillCode);
    }

    //秒杀抢购请求，给消息队列发送消息
    @RequestMapping("auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId, @RequestParam("skuIdStr") String skuIdStr,HttpServletRequest request){
        //检验库存状态
        String status = (String) CacheHelper.get(skuId + "");
        if(status.equals("0")|| StringUtils.isEmpty(status)){
            return Result.fail().message("库存已售完");
        }
        String userId = request.getHeader("userId");
        //检验抢购码
        String seckillCode = MD5.encrypt(skuId + userId);
        if(!skuIdStr.equals(seckillCode)){
            return Result.fail().message("非法请求");
        }
        //获取分布式锁
        boolean flag = activityService.getRedisLock(userId);
        //抢购请求
        if(flag){
            activityService.seckillOrder(userId,skuId);
            return Result.ok();
        }
        return Result.fail();
    }

    //检查抢购结果
    @RequestMapping("auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId,HttpServletRequest request){
        String userId = request.getHeader("userId");
        //已下单
        OrderRecode orderRecode = activityService.checkPlaceAnOrder(userId);
        if(orderRecode!=null){
            return Result.build(orderRecode.getOrderStr(),ResultCodeEnum.SECKILL_ORDER_SUCCESS);
        }
        //抢购成功未支付
        OrderRecode checkOrderRecode = activityService.checkOrderRecode(userId);
        if(checkOrderRecode!=null){
            return Result.build(null,ResultCodeEnum.SECKILL_SUCCESS);
        }
        //售罄
        String status = (String) CacheHelper.get(skuId + "");
        if(StringUtils.isEmpty(status) || status.equals("0")){
            return Result.build(null,ResultCodeEnum.SECKILL_FINISH);
        }
        //排队中
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }

    //查询预支付订单信息
    @RequestMapping("getOrderRecode/{userId}")
    OrderRecode getOrderRecode(@PathVariable("userId") String userId){
        OrderRecode orderRecode = activityService.checkOrderRecode(userId);
        return orderRecode;
    }

    //生成真实订单
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order,HttpServletRequest request){
        String userId = request.getHeader("userId");
        order.setUserId(Long.parseLong(userId));
        String orderId = activityService.submitOrder(order);
        return Result.ok(orderId);
    }
}
