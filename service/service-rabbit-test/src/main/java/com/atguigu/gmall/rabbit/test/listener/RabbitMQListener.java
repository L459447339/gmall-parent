package com.atguigu.gmall.rabbit.test.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQListener {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange("exchange-demo01"),
            key = {"demo01"},
            value = @Queue("queue-demo01"),
            declare = "false"
    ))
    public void consumeMsg(Message message, Channel channel) throws IOException {
        //获取消息
        byte[] body = message.getBody();
        String msg = new String(body);
        System.out.println("获取的消息是："+msg);
        //消息偏移量
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //手动确认消息，第二个参数表示是否批量确认
        channel.basicAck(deliveryTag,true);
    }

}
