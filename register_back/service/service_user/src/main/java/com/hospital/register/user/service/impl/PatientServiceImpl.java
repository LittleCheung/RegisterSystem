package com.hospital.register.user.service.impl;

import com.hospital.register.cmn.client.DictFeignClient;
import com.hospital.register.enums.DictEnum;
import com.hospital.register.model.user.Patient;
import com.hospital.register.user.mapper.PatientMapper;
import com.hospital.register.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    /**
     * 远程调用数据字典
     */
    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 根据用户id查询就诊人列表
     * @param userId
     * @return
     */
    @Override
    public List<Patient> findAllByUserId(Long userId) {
        //根据userid查询所有就诊人信息列表
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        //通过远程调用，得到具体内容，查询数据字典
        patientList.stream().forEach(item ->{
            this.packPatient(item);
        });

        return patientList;
    }

    /**
     * 根据id查询就诊人信息并进行返回，返回具有完整信息的对象
     * @param id 就诊人id
     * @return
     */
    @Override
    public Patient getPatientById(Long id) {
        Patient patient = baseMapper.selectById(id);
        return this.packPatient(patient);
    }



    /**
     * patient对象里面其他信息param的封装
     * @param patient
     * @return
     */
    private Patient packPatient(Patient patient){
        //根据证件类型编码，获取就诊人证件类型
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());

        //联系人证件类型
        String contactsCertificatesTypeString  =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());
        //省、市、区
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        String cityString = dictFeignClient.getName(patient.getCityCode());
        String districtString = dictFeignClient.getName(patient.getDistrictCode());

        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
