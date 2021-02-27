package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.PaymentWay;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.order.OrderDetail;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CartFeignClient cartFeignClient;


    @Override
    public String submitOrder(OrderInfo order) {
        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            Integer skuNum = orderDetail.getSkuNum();
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            totalAmount = totalAmount.add(new BigDecimal(skuNum+"").multiply(orderPrice));
        }
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.UNPAID.getComment());
        //第三方支付订单编号，全局唯一
        String outTradeNo = UUID.randomUUID().toString()+System.currentTimeMillis() + "";
        order.setOutTradeNo(outTradeNo);
        order.setCreateTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR,1);
        Date time = calendar.getTime();
        order.setExpireTime(time);
        order.setProcessStatus(ProcessStatus.UNPAID.getComment());
        order.setPaymentWay(PaymentWay.ONLINE.getComment());
        order.setImgUrl(orderDetailList.get(0).getImgUrl());
        order.setOrderComment("iphone12红色");
        //验证库存和商品价格与后台数据是否一致
        //TODO
        orderInfoMapper.insert(order);
        Long orderId = order.getId();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insert(orderDetail);
        }
        //添加成功后将原购物车的商品信息删除
        Long userId = order.getUserId();
        cartFeignClient.deleteCartOrder(userId);
        return orderId+"";
    }

    @Override
    public String genTradeNo(String userId) {
        String tradeNo = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RedisConst.USER_KEY_PREFIX+userId+":tradeNo",tradeNo,60L, TimeUnit.MINUTES);
        return tradeNo;
    }

    @Override
    public boolean checkTradeNo(String tradeNo, String userId) {
        boolean flag = false;
        String tradeNoCeche = (String)redisTemplate.opsForValue().get(RedisConst.USER_KEY_PREFIX + userId+":tradeNo");
        if(!StringUtils.isEmpty(tradeNo) && tradeNo.equals(tradeNoCeche)){
            flag = true;
            //并删除redis中的tradeNo
            redisTemplate.delete(RedisConst.USER_KEY_PREFIX + userId + ":tradeNo");
        }
        return flag;
    }

    @Override
    public OrderInfo getOrderById(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //将OrderDatail设置到orderInfo中
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(queryWrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    //修改订单状态为已支付
    @Override
    public void updateStatus(String msg) {
        Map<String,Object> map = JSON.parseObject(msg, Map.class);
        Long orderId = Long.parseLong(map.get("orderId")+"");
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        orderInfoMapper.updateById(orderInfo);
        //发送消息锁定库存
        //TODO
        Map<String, Object> mapMessage = new HashMap<>();
        String mapMessageToString = JSON.toJSONString(mapMessage);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_WARE_ORDER,MqConst.ROUTING_WARE_ORDER,mapMessageToString);
    }
}
