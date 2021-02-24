package com.atguigu.gmall.payment.service;

public interface PaymentService {
    String tradePagePay(Long orderId);

    void updatePayment(String tradeNo, String outTradeNo,String callbackContent);

    boolean checkPaymentStatus(String out_trade_no);
}
