package com.hospital.register.hosp.client;

import com.hospital.register.vo.hosp.ScheduleOrderVo;
import com.hospital.register.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 医院管理模块服务调用封装
 * TODO 需要实现服务降级熔断时，创建该接口实现类
 * @author littlecheung
 */
@Component
@FeignClient("service-hosp")
public interface HospitalFeignClient {

    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId 排班id
     * @return
     */
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);


    /**
     * 根据医院编号获取医院签名信息
     * @param hoscode 医院编号
     * @return
     */
    @ApiOperation("获取医院签名信息")
    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);

}
