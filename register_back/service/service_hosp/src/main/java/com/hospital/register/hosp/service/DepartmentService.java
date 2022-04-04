package com.hospital.register.hosp.service;

import com.hospital.register.model.hosp.Department;
import com.hospital.register.vo.hosp.DepartmentQueryVo;
import com.hospital.register.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    /**
     * 上传科室信息
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 查询科室的接口
     * @param page 当前页
     * @param limit 分页数
     * @param departmentQueryVo 查询对象
     * @return
     */
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     * @param hoscode
     * @param depcode
     */
    void remove(String hoscode, String depcode);

    /**
     * 根据hoscode查询医院所有科室,及其子科室
     * @param hoscode
     * @return
     */
    List<DepartmentVo> findDeptTree(String hoscode);

    /**
     * 根据医院编号、科室编号查询科室名称
     * @param hoscode
     * @param depcode
     * @return
     */
    String getDepName(String hoscode, String depcode);

    /**
     * 根据hoscode, depcode查询科室信息并返回科室对象
     * @param hoscode
     * @param depcode
     * @return
     */
    Department getDepartment(String hoscode, String depcode);
}
