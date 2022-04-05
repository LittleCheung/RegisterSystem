package com.hospital.register.hosp.mongoRepository;

import com.hospital.register.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 科室操作
 * @author littlecheung
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    /**
     * 根据医院编号和科室编号获取具体科室信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
