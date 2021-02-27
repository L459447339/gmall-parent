package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.enums.PaymentStatus;
import com.atguigu.gmall.enums.PaymentType;
import com.atguigu.gmall.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayClientConfig;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    //注入支付宝客户端
    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //生成支付宝二维码,此时支付宝无状态
    @Override
    public String tradePagePay(Long orderId) {

        OrderInfo orderInfo = orderFeignClient.getOrderById(orderId);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        Map<String,Object> map = new HashMap<>();
        //第三方订单号
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        //销售产品码，仅支持FAST_INSTANT_TRADE_PAY
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        //商品价格
        map.put("total_amount",0.01);
        //商品描述
        map.put("subject",orderInfo.getOrderComment());
        request.setBizContent(JSON.toJSONString(map));
        //回调地址
        request.setNotifyUrl(AlipayClientConfig.notifyPaymentUrl);
        request.setReturnUrl(AlipayClientConfig.returnPaymentUrl);
        String from = "";
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            from = response.getBody();
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //将支付信息保存db
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getOrderComment());
        paymentInfo.setPaymentType(PaymentType.ALIPAY.getComment());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderId);
        paymentInfo.setCreateTime(new Date());
        paymentInfoMapper.insert(paymentInfo);
        return from;
    }

    @Override
    public void updatePayment(String tradeNo, String outTradeNo ,String callbackContent) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",outTradeNo);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);
        paymentInfo.setTradeNo(tradeNo);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo.setCallbackContent(callbackContent);
        paymentInfoMapper.updateById(paymentInfo);
        //发送消息修改订单状态
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",paymentInfo.getOrderId());
        String msg = JSON.toJSONString(map);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,MqConst.ROUTING_PAYMENT_PAY,msg);
        System.out.println("发送修改信息状态消息成功");
    }

    @Override
    public boolean checkPaymentStatus(String out_trade_no) {
        boolean flag = false;
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021001163617452","MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8Z7EZmanxyFGsK4LrIUeKKrrGxWAHIgPmUV8TtZDs+jeplJSw1ckSY63QhEU444D5qd6xruJHBuB33HG+ik4n8N8nRWi3AtMgpC061oq2DcgtIKMmQHO7/poYDwbpDZrOWXIyiNshFfUOSTUpnrS8UvEks6n6xR/G72r2FG07oZzO7g3XsPMr73wpYajMYC/bhTm6CJGEWZikONNDFkQpVHa+zgitwsqlBuvBvVwGwOHA9B8aRfokwAMl6BDXKoH8BNnSEMpWSTRSwbssayXAQWNU7XKDKGozbn4U2dEbl8GCFzikI/T7ybTNm5gs46ZZBGlq/YB4+v4D3t74Vl6nAgMBAAECggEAOidzhehliYkAlLk1huhV0bMQxewEkQ8RzxTM2SORIWS2q7R+FPtYPkHgU92QFFg85lNltsi5dZ0MylKUFXFRYIi8CL4m7V6E1q12fJPeawVkBXHuig8Y6i1TWRvCUUtuvkTjt++AW/0QECHOtBMVzI95eY+vZwVToq8h/+UcNmxKyVt66Qpo4+r+cUvlvGX5mXgQVC5Ftf/MtHA1i+kjtzBITC0xAvmSXKzjN1YhtcS9rXyMHXBiFhXLdmvOXjkn0Okosr2+tmesXfSwDGhH3ZlOdHzit4D602RNl0nTA1dOUWHuCncs1TrWbriax86P/EYvmzMiHWCVTmmNJC0bMQKBgQD0HAXKNsYsdjCQOV4t3SMqOKaul67x/KA20PmMZVfQ2sQkyjyFgWpL8C16Rzf3zI7df+zF5SkvhFY4+LRZVwX5okEFYTzAZ/NYouj1/DABYOPq0E0sY18/xtq7FJ/CIk8qmCqcczqoyaoxoaC1zAt9E4CYE89iEOnO+GhcI3H3LwKBgQDFlQzvbXhWRyRFkeft/a52XLnyj6t9iP7wNGbGCSeoMDrAu3ZgoqacUPWj5MgSFZdT48H9rF4pPixXoe3jfUNsWBUHqD1F2drDz7lpL0PbpSsgy6ei+D4RwTADsuyXwrkvrWrGro+h6pNJFyly3nea/gloDtJTzfhFFwtNfmqyCQKBgBXzMx4UwMscsY82aV6MZO4V+/71CrkdszZaoiXaswPHuB1qxfhnQ6yiYyR8pO62SR5ns120Fnj8WFh1HJpv9cyVp20ZakIO1tXgiDweOh7VnIjvxBC6usTcV6y81QS62w2Ec0hwIBUvVQtzciUGvP25NDX4igxSYwPGWHP4h/XnAoGAcQN2aKTnBgKfPqPcU4ac+drECXggESgBGof+mRu3cT5U/NS9Oz0Nq6+rMVm1DpMHAdbuqRikq1aCqoVWup51qE0hikWy9ndL6GCynvWIDOSGrLWQZ2kyp5kmy5bWOWAJ6Ll6r7Y9NdIk+NOkw614IFFaNAj2STUw4uPxdRvwD3ECgYEArwOZxR3zl/FZfsvVCXfK8/fhuZXMOp6Huwqky4tNpVLvOyihpOJOcIFj6ZJhoVdmiL8p1/1S+Sm/75gx1tpFurKMNcmYZbisEC7Ukx7RQohZhZTqMPgizlVBTu5nR3xkheaJC9odvyjrWQJ569efXo30gkW04aBp7A15VNG5Z/U=","json","GBK","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkWs+3gXMosiWG+EbfRyotWB0waqU3t7qMQSBxU0r3JZoND53jvWQfzrGZ8W+obMc+OgwupODDVxhG/DEKVBIptuUQYdvAjCSH98m2hclFcksspuCy9xS7PyflPE47pVzS6vA3Slvw5OFQ2qUcku4paWnBxguLUGPjEncij5NcyFyk+/k57MmrVJwCZaI+lFOS3Eq2IXc07tWXO4s/2SWr3EJiwJutOGBdA1ddvv1Urrl0pWpEFg30pJB6J7YteuxdEL90kuO5ed/vnTK5qgQRvEelROkUW44xONk1784v28OJXmGICmNL1+KyM/SFbFOSgJZSV1tEXUzvL/xvzFpLwIDAQAB","RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no",out_trade_no);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
            String tradeStatus = response.getTradeStatus();
            if(tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")){
                flag = true;
            }
        } else {
            System.out.println("调用失败");
        }
        return flag;
    }
}
