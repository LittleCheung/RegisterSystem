package com.hospital.register.user.controller.api;


import com.hospital.register.common.result.Result;
import com.hospital.register.common.utils.AuthContextHolder;
import com.hospital.register.model.user.Patient;
import com.hospital.register.user.service.PatientService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api("就诊人管理接口")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    /**
     * 获取就诊人列表
     * @param request
     * @return
     */
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list =  patientService.findAllByUserId(userId);

        return Result.ok(list);
    }

    /**
     * 添加就诊人
     * @param patient 就诊人
     * @param request
     * @return
     */
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient,HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    /**
     * 根据id获取就诊人信息
     * @param id 就诊人id
     * @return
     */
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id){
        Patient patient =  patientService.getPatientById(id);
        return Result.ok(patient);
    }

    /**
     * 修改就诊人信息
     * @param patient 就诊人信息
     * @return
     */
    @PutMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    /**
     * 删除就诊人信息
     * @param id 就诊人id
     * @return
     */
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id){
        patientService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据就诊人id获取就诊人信息
     * @param id 就诊人id
     * @return
     */
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable Long id){
        Patient patient = patientService.getPatientById(id);
        return patient;
    }
}
