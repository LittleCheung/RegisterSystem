package com.hospital.register.task.scheduled;

import com.hospital.common.rabbit.constant.MqConst;
import com.hospital.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 *
 * “@EnableScheduling”：表示开启定时任务，cron表达式设置执行间隔
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 设置每天8点提醒就诊
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void taskPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
