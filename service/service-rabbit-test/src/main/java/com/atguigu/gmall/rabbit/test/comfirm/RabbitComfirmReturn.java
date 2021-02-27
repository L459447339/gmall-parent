package com.atguigu.gmall.rabbit.test.comfirm;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitComfirmReturn implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //构造器调用完成创建对象时执行此方法
    @PostConstruct
    public void rabbit(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     correlationData  携带的其他数据，可能是消息唯一Id
     b 表示消息是否成功发送到broker，只要成功发送就为true
     s  表示消息的错误信息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("确认...");
    }

    /**
     消息从交换机投递队列失败时触发此回调方法
     message 表示消息
     i  回复的状态码
     s  回复的文本内容，一般都是错误信息的描述
     s1 哪个交换机投递的消息
     s2 投递消息使用的是哪个路由键
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("回调...");
    }
}
