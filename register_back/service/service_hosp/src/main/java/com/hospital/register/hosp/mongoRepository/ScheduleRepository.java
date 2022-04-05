package com.hospital.register.hosp.mongoRepository;

import com.hospital.register.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * 排班操作
 * @author littlecheung
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    /**
     * 根据医院编号和医院排班id获取排班信息
     * @param hoscode 医院编号
     * @param hosScheduleId 医院排班id
     * @return
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    /**
     * 根据医院编号、科室编号和具体日期查询排班信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param toDate 具体日期
     * @return
     */
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
