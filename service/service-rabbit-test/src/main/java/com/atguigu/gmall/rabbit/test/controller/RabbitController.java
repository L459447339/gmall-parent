package com.atguigu.gmall.rabbit.test.controller;

import com.atguigu.gmall.rabbit.test.service.RabbitService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitController {

    @Autowired
    RabbitService rabbitService;

    @Autowired
    AmqpAdmin amqpAdmin;

    @RequestMapping("rabbit/test")
    public String rabbit(){
        //发送消息
        rabbitService.sendMassage();
        return "rabbitTest";
    }

    //创建交换机
    @RequestMapping("createExchange")
    public String createExchange(){
        /**
         * 点对点交换机
         * String name  交换机名称
         * boolean durable  是否持久化
         * boolean autoDelete   是否自动删除
         * Map<String, Object> arguments    携带的其他的参数
         */
        DirectExchange directExchange = new DirectExchange("exchange-demo01",true,false);
        amqpAdmin.declareExchange(directExchange);
        return "创建exchange-demo01成功";
    }

    //创建消息队列
    @RequestMapping("createQueue")
    public String createQueue(){
        /**
         * 创建队列
         * String name  队列名称
         * boolean durable  是否持久化
         * boolean exclusive    是否是排它队列
         * boolean autoDelete   是否自动删除
         * Map<String, Object> arguments)   携带其他的参数
         */
        Queue queue = new Queue("queue-demo01",true,false,false);
        amqpAdmin.declareQueue(queue);
        return "创建queue-demo01成功";
    }

    //创建binding对象
    @RequestMapping("createBinding")
    public String createBinding(){
        /**
         * 创建绑定规则对象
         * String destination   目的地，可以是交换机名称也可以是队列名称
         * Binding.DestinationType destinationType  目的地类型，交换机或队列
         * String exchange  绑定的交换机名称
         * String routingKey    路由键名
         * Map<String, Object> arguments    自定义参数
         */
        Binding binding = new Binding("queue-demo01", Binding.DestinationType.QUEUE,"exchange-demo01","demo01",null);
        amqpAdmin.declareBinding(binding);
        return "创建binding-demo01成功";
    }

    //发送延迟消息
    @RequestMapping("sendDalayMsg")
    public String sendDalayMsg(){
        rabbitService.sendDalayMsg();
        return "发送sendDalayMsg";
    }


}
