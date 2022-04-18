package com.hospital.register.hosp.recevier;


import com.hospital.common.rabbit.constant.MqConst;
import com.hospital.common.rabbit.service.RabbitService;
import com.hospital.register.hosp.service.ScheduleService;
import com.hospital.register.model.hosp.Schedule;
import com.hospital.register.vo.msm.MsmVo;
import com.hospital.register.vo.order.OrderMqVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * MQ监听器：用于mq监听，对预约数进行更新
 */
@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;


    /**
     * 用于mq监听，进行更新剩余预约数等，并进行短信发送，通知用户
     * @param orderMqVo 排班更新实体
     * @param message 消息
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
                                            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
                                            key = {MqConst.ROUTING_ORDER}))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        if(null != orderMqVo.getAvailableNumber()) {
            //下单成功更新预约数
            Schedule schedule = scheduleService.getScheduleById(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);
        } else {
            //取消预约更新预约数
            Schedule schedule = scheduleService.getScheduleById(orderMqVo.getScheduleId());
            int availableNumber = schedule.getAvailableNumber().intValue() + 1;
            schedule.setAvailableNumber(availableNumber);
            scheduleService.update(schedule);
        }
        //发送短信，下单或者取消预约都发信息给用户
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }

    }


}
