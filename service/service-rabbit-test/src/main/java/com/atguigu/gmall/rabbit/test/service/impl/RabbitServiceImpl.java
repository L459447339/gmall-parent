package com.atguigu.gmall.rabbit.test.service.impl;

import com.atguigu.gmall.rabbit.test.service.RabbitService;
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
}