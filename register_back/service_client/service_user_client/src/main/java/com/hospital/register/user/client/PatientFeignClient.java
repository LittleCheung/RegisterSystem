package com.hospital.register.user.client;

import com.hospital.register.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 排班下单信息接口远程调用
 * TODO 需要实现服务降级熔断时，创建该接口实现类
 * @author littlecheung
 */
@Component
@FeignClient("service-user")
public interface PatientFeignClient {

    /**
     * 根据就诊人id获取就诊人信息
     * @param id 就诊人id
     * @return
     */
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);
}
