package com.hospital.register.hosp.service;

import com.hospital.register.model.hosp.Schedule;
import com.hospital.register.vo.hosp.ScheduleOrderVo;
import com.hospital.register.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    /**
     * 保存排班
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 查询排班
     * @param page
     * @param limit
     * @param scheduleQueryVo
     * @return
     */
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    /**
     * 删除排班
     * @param hoscode
     * @param hosScheduleId
     */
    void remove(String hoscode, String hosScheduleId);

    /**
     * 据hoscode和depcode查询排班规则，并进行分页
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    /**
     * 根据 {page}/{limit}/{hoscode}/{depcode} 获取可预约排班数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    /**
     * 根据排班id获取排班信息，返回排班对象
     * @param scheduleId
     * @return
     */
    Schedule getScheduleById(String scheduleId);

    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId
     * @return
     */
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    /**
     * 下单后，更新排班数据
     * @param schedule
     */
    void update(Schedule schedule);

}
