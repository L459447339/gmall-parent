package com.atguigu.gmall.order.receive;

import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PayReceive {

    @Autowired
    OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = {MqConst.ROUTING_PAYMENT_PAY},
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY,autoDelete = "false",exclusive = "false")
    ))
    public void receive(Channel channel, Message message,String msg){
        System.out.println("监听支付回调....");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //手动确认
            channel.basicAck(deliveryTag,false);
            //修改订单状态
            orderService.updateStatus(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
