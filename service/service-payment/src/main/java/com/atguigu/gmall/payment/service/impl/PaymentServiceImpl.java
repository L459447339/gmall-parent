package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    //注入支付宝客户端
    @Autowired
    AlipayClient alipayClient;

    @Override
    public String tradePagePay(Long orderId) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        Map<String,Object> map = new HashMap<>();
        //第三方订单号
        map.put("out_trade_no","87ec921b-2751-4d4e-963c-693ed65f93ee1614077210643");
        //销售产品码，仅支持FAST_INSTANT_TRADE_PAY
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        //商品价格
        map.put("total_amount",0.01);
        //商品描述
        map.put("subject","iphone12");
        request.setBizContent(JSON.toJSONString(map));
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
           return  response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
