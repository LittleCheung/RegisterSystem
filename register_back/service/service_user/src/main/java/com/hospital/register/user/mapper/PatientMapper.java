package com.hospital.register.user.mapper;

import com.hospital.register.model.user.Patient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
