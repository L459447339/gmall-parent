package com.atguigu.gmall.order.receive;

import com.alibaba.fastjson.JSON;
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

import java.util.Map;


@Component
public class WareOrderReceive {

    @Autowired
    OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_WARE_ORDER),
            key = {MqConst.ROUTING_WARE_ORDER},
            value = @Queue(value = MqConst.QUEUE_WARE_ORDER,autoDelete = "false",exclusive = "false")
    ))
    public void receive(Channel channel, Message message, String msg) throws Exception {
        Map<String,Object> map = JSON.parseObject(msg, Map.class);
        Long orderId = Long.parseLong(map.get("orderId")+"");
        String status = (String) map.get("status");
        //调用service层修改订单状态为已出货
        orderService.updateWareStatus(orderId,status);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
    }

}
