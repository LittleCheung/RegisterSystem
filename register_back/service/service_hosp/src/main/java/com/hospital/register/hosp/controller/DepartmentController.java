package com.hospital.register.hosp.controller;


import com.hospital.register.common.result.Result;
import com.hospital.register.hosp.service.DepartmentService;
import com.hospital.register.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理科室请求
 */
@Api("科室展示接口")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据医院编号查询医院所有科室列表
     * @param hoscode 医院编号
     * @return
     */
    @ApiOperation("查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> list =  departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}
