package com.atguigu.gmall.activity.reciver;

import com.atguigu.gmall.activity.service.ActivityService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitSeckillReciver {

    @Autowired
    ActivityService activityService;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_SECKILL_USER),
            key = {MqConst.ROUTING_SECKILL_USER},
            value = @Queue(value = MqConst.QUEUE_SECKILL_USER,autoDelete = "false",exclusive = "false")
    ))
    public void reciver(Channel channel, Message message,String userToJsonString) throws IOException {
        activityService.reciverSeckillGoods(userToJsonString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
