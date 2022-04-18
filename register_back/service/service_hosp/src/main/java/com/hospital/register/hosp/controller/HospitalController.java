package com.hospital.register.hosp.controller;


import com.hospital.register.common.result.Result;
import com.hospital.register.hosp.service.HospitalService;
import com.hospital.register.model.hosp.Hospital;
import com.hospital.register.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 处理医院信息管理请求
 * @author littlecheung
 */
@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 医院列表条件分页查询接口
     * @param page 当前页
     * @param limit 每页记录数
     * @param hospitalQueryVo 医院信息类
     * @return
     */
    @ApiOperation("医院列表分页")
    @GetMapping("list/{page}/{limit}")
    public Result listHospital(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel =  hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 更新医院上线状态接口
     * @param id
     * @param status 状态
     * @return
     */
    @ApiOperation("更新医院上线状态接口")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status){

        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    /**
     * 获取医院详情信息接口
     * @param id
     * @return
     */
    @ApiOperation("医院详情信息接口")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String,Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
