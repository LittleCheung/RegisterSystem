package com.hospital.register.user.service;

import com.hospital.register.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PatientService extends IService<Patient> {
    /**
     * 根据用户id查询就诊人列表
     * @param userId
     * @return
     */
    List<Patient> findAllByUserId(Long userId);

    /**
     * 根据id查询就诊人信息，并进行返回，返回的是具有完整信息的对象
     * @param id
     * @return
     */
    Patient getPatientById(Long id);
}
