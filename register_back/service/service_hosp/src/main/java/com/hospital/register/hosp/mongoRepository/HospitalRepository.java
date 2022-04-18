package com.hospital.register.hosp.mongoRepository;

import com.hospital.register.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 医院操作
 * 注意：MongoRepository按照Spring Data的规范自动执行
 * @author littlecheung
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    /**
     * 根据医院编号获取医院信息,判断数据是否存在
     * @param hoscode 医院编号
     * @return
     */
    Hospital getHospitalByHoscode(String hoscode);


    /**
     * 根据医院名称查询医院信息
     * @param hosname 医院名称
     * @return
     */
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
