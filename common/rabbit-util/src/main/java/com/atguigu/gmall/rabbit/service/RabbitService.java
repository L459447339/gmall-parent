package com.atguigu.gmall.rabbit.service;

import ch.qos.logback.core.util.TimeUtil;
import io.micrometer.core.instrument.util.TimeUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RabbitService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void SendMessage(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    public void SendDelayMessage(String exchange, String routingKey, String message, Integer time,TimeUnit unit){
        if(unit.compareTo(TimeUnit.SECONDS)==0){
            rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setDelay(time*1000);
                    return message;
                }
            });
        }
    }
}
