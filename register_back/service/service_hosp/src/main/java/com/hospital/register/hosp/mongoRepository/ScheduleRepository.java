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


    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
