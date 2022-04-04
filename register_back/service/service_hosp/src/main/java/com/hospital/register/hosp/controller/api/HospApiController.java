package com.hospital.register.hosp.controller.api;

import com.hospital.register.common.result.Result;
import com.hospital.register.hosp.service.DepartmentService;
import com.hospital.register.hosp.service.HospitalService;
import com.hospital.register.hosp.service.HospitalSetService;
import com.hospital.register.hosp.service.ScheduleService;
import com.hospital.register.model.hosp.Hospital;
import com.hospital.register.model.hosp.Schedule;
import com.hospital.register.vo.hosp.DepartmentVo;
import com.hospital.register.vo.hosp.HospitalQueryVo;
import com.hospital.register.vo.hosp.ScheduleOrderVo;
import com.hospital.register.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api("医院首页接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;



    /**
     * 查询医院列表并进行分页
     * @param page 当前页
     * @param limit 每页记录数
     * @param hospitalQueryVo 医院查询对象
     * @return
     */
    @ApiOperation("查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    /**
     * 根据医院名称查询医院信息
     * @param hosname 医院名称
     * @return
     */
    @ApiOperation("根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }

    /**
     * 根据医院编号获取科室
     * @param hoscode 医院编号
     * @return
     */
    @ApiOperation("根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }

    /**
     * 根据医院编号获取医院预约挂号详情
     * @param hoscode 医院编号
     * @return
     */
    @ApiOperation("根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode) {
        Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }


    /**
     * 获取可预约排班数据
     * @param page 当前页
     * @param limit 每页记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return
     */
    @ApiOperation("获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Integer page,
                                     @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable Integer limit,
                                     @ApiParam(name = "hoscode", value = "医院编号", required = true) @PathVariable String hoscode,
                                     @ApiParam(name = "depcode", value = "科室编号", required = true) @PathVariable String depcode) {
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    /**
     * 获取排班数据
     * @param hoscode 医院编号
     * @param depcode 部门编号
     * @param workDate 排班日期
     * @return
     */
    @ApiOperation("获取排班具体数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院编号", required = true) @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室编号", required = true) @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true) @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

    /**
     * 根据排班id获取排班信息
     * @param scheduleId 排班id
     * @return
     */
    @ApiOperation("根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable String scheduleId){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return Result.ok(schedule);
    }

    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId 排班id
     * @return
     */
    @ApiOperation("根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@ApiParam(name = "scheduleId", value = "排班id", required = true)
                                                  @PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    /**
     * 根据hoscode获取医院签名信息
     * @param hoscode 医院编号
     * @return
     */
    @ApiOperation("获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(@ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode){
        return hospitalSetService.getSignInfoVo(hoscode);
    }
}