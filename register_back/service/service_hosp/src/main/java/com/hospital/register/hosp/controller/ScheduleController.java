package com.hospital.register.hosp.controller;


import com.hospital.register.common.result.Result;
import com.hospital.register.hosp.service.ScheduleService;
import com.hospital.register.model.hosp.Schedule;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("排班管理接口")
@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据hoscode和depcode查询排班规则，并进行分页
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @ApiOperation("查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page, @PathVariable long limit,
                                  @PathVariable String hoscode, @PathVariable String depcode){
        Map<String, Object> map = scheduleService.getScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @ApiOperation("查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> scheduleList =  scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return  Result.ok(scheduleList);
    }
}
