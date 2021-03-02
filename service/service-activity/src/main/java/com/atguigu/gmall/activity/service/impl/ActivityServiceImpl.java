package com.atguigu.gmall.activity.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.activity.OrderRecode;
import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.activity.mepper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.rabbit.service.RabbitService;
import com.atguigu.gmall.user.UserRecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitService rabbitService;

    @Autowired
    OrderFeignClient orderFeignClient;

    @Override
    public List<SeckillGoods> getSeckillGoodsList() {
        List<SeckillGoods> seckillGoodsList = (List<SeckillGoods>)redisTemplate.opsForHash().values(RedisConst.SECKILL_GOODS);
        return seckillGoodsList;
    }

    @Override
    public void seckillGoodsPut() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(null);
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            Integer stockCount = seckillGoods.getStockCount();
            for (int i = 0; i < stockCount; i++) {
                //将秒杀商品库存添加到list中
                redisTemplate.opsForList().leftPush(RedisConst.SECKILL_STOCK_PREFIX + seckillGoods.getSkuId(), seckillGoods.getSkuId() + "");
            }
            //将秒杀商品列表添加到hash
            redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS, seckillGoods.getSkuId() + "", seckillGoods);
            //通知秒杀服务商品状态
            String status = seckillGoods.getStockCount() > 0 ? "1" : "0";
            redisTemplate.convertAndSend("seckillpush", seckillGoods.getSkuId() + ":" + status);
        }
    }

    //从redis中获取秒杀商品详情
    @Override
    public SeckillGoods getSeckillGoods(Long skuId) {
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId + "");
        return seckillGoods;
    }

    //发送抢购消息
    @Override
    public void seckillOrder(String userId, Long skuId) {
        boolean flag = false;
        Map<String,Object> map = new HashMap<>();
        UserRecode userRecode = new UserRecode();
        userRecode.setSkuId(skuId);
        userRecode.setUserId(userId);
        String userToJsonString = JSON.toJSONString(userRecode);
        rabbitService.SendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER,MqConst.ROUTING_SECKILL_USER,userToJsonString);
    }


    @Override
    public void reciverSeckillGoods(String userToJsonString) {
        UserRecode userRecode = JSON.parseObject(userToJsonString, UserRecode.class);
        String userId = userRecode.getUserId();
        //从秒杀库中抢购
        String skuId = (String) redisTemplate.opsForList().rightPop(RedisConst.SECKILL_STOCK_PREFIX + userRecode.getSkuId());
        if(StringUtils.isEmpty(skuId)){
           //库存售完，抢购失败，修改库存状态
            redisTemplate.convertAndSend("seckillpush",userRecode.getSkuId()+":"+"0");
        }else {
            //抢购成功，生成预订单
            OrderRecode orderRecode = new OrderRecode();
            orderRecode.setUserId(userId);
            orderRecode.setNum(1);
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId);
            orderRecode.setSeckillGoods(seckillGoods);
            //将预订单保存在redis中
            redisTemplate.opsForHash().put(RedisConst.SECKILL_ORDERS,userId,orderRecode);
        }
    }

    @Override
    public boolean getRedisLock(String userId) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_USER + userId + ":lock", "lockVal",20, TimeUnit.SECONDS);
        return flag;
    }

    @Override
    public OrderRecode checkPlaceAnOrder(String userId) {
        OrderRecode orderRecode = (OrderRecode) redisTemplate.opsForHash().get(RedisConst.SECKILL_ORDERS_USERS, userId);
        return orderRecode;
    }

    @Override
    public OrderRecode checkOrderRecode(String userId) {
        OrderRecode orderRecode = (OrderRecode) redisTemplate.opsForHash().get(RedisConst.SECKILL_ORDERS, userId);
        return orderRecode;
    }

    @Override
    public String submitOrder(OrderInfo order) {
        //调用order微服务生成订单
        String orderId = orderFeignClient.submitOrderSeckill(order);
        //在redis中保存订单状态信息用于检查结果中鉴别订单状态
        OrderRecode orderRecode = new OrderRecode();
        orderRecode.setNum(1);
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, order.getOrderDetailList().get(0).getSkuId() + "");
        orderRecode.setSeckillGoods(seckillGoods);
        orderRecode.setUserId(order.getUserId()+"");
        orderRecode.setOrderStr(UUID.randomUUID().toString());
        redisTemplate.opsForHash().put(RedisConst.SECKILL_ORDERS_USERS,order.getUserId()+"",orderRecode);
        //删除redis中的预支付订单
        redisTemplate.opsForHash().delete(RedisConst.SECKILL_ORDERS,order.getUserId()+"");
        return orderId;
    }
}
