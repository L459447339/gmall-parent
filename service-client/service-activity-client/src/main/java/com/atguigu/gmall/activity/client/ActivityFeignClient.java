package com.atguigu.gmall.activity.client;

import com.atguigu.gmall.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-activity")
public interface ActivityFeignClient {

    @RequestMapping("api/activity/seckill/getSeckillGoodsList")
    List<SeckillGoods> getSeckillGoodsList();
}
