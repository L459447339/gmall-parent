package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.PaymentWay;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.order.OrderDetail;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public String submitOrder(String tradeNo, OrderInfo order) {
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
        String outTradeNo = UUID.randomUUID().toString() + System.currentTimeMillis() + "";
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
        orderInfoMapper.insert(order);
        Long userId = order.getId();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(userId);
            orderDetailMapper.insert(orderDetail);
        }

        return userId+"";
    }
}
