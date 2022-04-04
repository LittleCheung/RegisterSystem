package com.hospital.register.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.register.cmn.client.DictFeignClient;
import com.hospital.register.hosp.mongoRepository.HospitalRepository;
import com.hospital.register.hosp.service.HospitalService;
import com.hospital.register.model.hosp.Hospital;
import com.hospital.register.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;


    /**
     * 上传医院信息
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map集合转换成对象Hospital
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist =  hospitalRepository.getHospitalByHoscode(hoscode);

        //如果存在进行修改
        if(hospitalExist != null){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
        }else{
            //不存在进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
        }
        hospital.setUpdateTime(new Date());
        hospital.setIsDeleted(0);
        hospitalRepository.save(hospital);
    }

    /**
     * 根据医院编号进行查询
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }


    /**
     * 医院信息条件分页查询
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        //创建pageable对象
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //将vo对象转换为pojo对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        //创建example对象
        Example<Hospital> example = Example.of(hospital, matcher);

        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //获取查询list集合，遍历进行医院等级信息封装
        pages.getContent().stream().forEach(item ->{
            this.setHospitalHosType(item);
        });
        return pages;
    }


    /**
     * 更新医院状态
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询hospital对象
        Hospital hospital = hospitalRepository.findById(id).get();

        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }


    /**
     * 通过id获取医院信息
     * @param id
     * @return
     */
    @Override
    public Map<String,Object> getHospById(String id) {
        //获取医院信息，包含了医院等级，地区等信息
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        Map<String, Object> result = new HashMap<>();
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }


    /**
     * 根据医院编号获取医院名称
     * @param hoscode
     * @return
     */
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null){
            return hospital.getHosname();
        }
        return null;
    }


    /**
     * 根据医院名称查询医院信息
     * @param hosname
     * @return
     */
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }


    /**
     * 根据医院编号获取医院预约挂号详情
     * @param hoscode
     * @return
     */
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }


    /**
     * 获取查询list集合，服务调用cmn模块获取医院等级、地区等信息
     * @param hospital
     * @return
     */
    public Hospital setHospitalHosType(Hospital hospital){
        //根据dictCode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省、市、地区信息
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString);
        return hospital;
    }
}
