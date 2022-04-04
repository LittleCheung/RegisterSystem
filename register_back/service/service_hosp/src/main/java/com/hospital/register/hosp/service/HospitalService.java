package com.hospital.register.hosp.service;

import com.hospital.register.model.hosp.Hospital;
import com.hospital.register.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    //上传医院接口
    void save(Map<String, Object> paramMap);

    /**
     * 根据医院编号进行查询
     * @param hoscode
     * @return
     */
    Hospital getByHoscode(String hoscode);

    /**
     * 条件分页查询
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 更新医院状态
     * @param id
     * @param status
     */
    void updateStatus(String id, Integer status);

    /**
     * 通过id获取医院信息
     * @param id
     * @return
     */
    Map<String,Object> getHospById(String id);

    /**
     * 根据医院编号获取医院名称
     * @param hoscode
     * @return
     */
    String getHospName(String hoscode);

    /**
     * 根据医院名称查询医院信息
     * @param hosname
     * @return
     */
    List<Hospital> findByHosname(String hosname);

    /**
     * 根据医院编号获取医院预约挂号详情
     * @param hoscode
     * @return
     */
    Map<String, Object> item(String hoscode);


}
