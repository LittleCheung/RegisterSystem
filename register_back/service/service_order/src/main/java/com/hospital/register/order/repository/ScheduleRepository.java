package com.hospital.register.order.repository;

import com.hospital.register.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule findScheduleById(String scheduleId);
}