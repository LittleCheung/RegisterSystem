package com.hospital.register.msm.receiver;

import com.hospital.common.rabbit.constant.MqConst;
import com.hospital.register.msm.service.MsmService;
import com.hospital.register.vo.msm.MsmVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsmReceiver {
    @Autowired
    private MsmService msmService;

    /**
     * MQ监听器：使用mq进行监听,进行短信发送
     * @param msmVo 短信实体对象
     */
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
            ))
    public void send(MsmVo msmVo){
        System.out.println("发送短信给用户啦~");
        msmService.send(msmVo);
    }

}
