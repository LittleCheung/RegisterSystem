package com.hospital.register.order.receiver;

import com.hospital.common.rabbit.constant.MqConst;
import com.hospital.register.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * mq监听器：用于监听就诊人订单消息
 */
@Service
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    /**
     * 使用mq进行监听
     * @param message 消息
     * @param channel 通道
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_8}
    ))
    public void  patientTips(Message message, Channel channel) throws IOException {
        orderService.patientTips();
    }
}
