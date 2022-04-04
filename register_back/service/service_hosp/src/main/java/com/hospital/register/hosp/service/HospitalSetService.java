package com.hospital.register.hosp.service;

import com.hospital.register.model.hosp.HospitalSet;
import com.hospital.register.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);

    /**
     * 根据 hoscode获取医院签名信息
     * @param hoscode
     * @return
     */
    SignInfoVo getSignInfoVo(String hoscode);
}
