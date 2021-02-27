package com.atguigu.gmall.rabbit.test.service.impl;

import com.atguigu.gmall.rabbit.test.service.RabbitService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitServiceImpl implements RabbitService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void sendMassage() {
        rabbitTemplate.convertAndSend("exchange-demo01","demo01","第一条消息...");
    }

    @Override
    public void sendDalayMsg() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("exchange.delay","routing.delay" , "我是延迟消息...",new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setDelay(10*1000);
                    return message;
                }
            });
        }
    }
}