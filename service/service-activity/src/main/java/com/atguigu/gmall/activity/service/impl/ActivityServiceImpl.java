package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.activity.mepper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.constant.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> getSeckillGoodsList() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(null);
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
}
