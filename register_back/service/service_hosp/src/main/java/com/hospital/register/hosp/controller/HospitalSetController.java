package com.hospital.register.hosp.controller;

import com.hospital.register.common.result.Result;
import com.hospital.register.common.utils.MD5;
import com.hospital.register.hosp.service.HospitalSetService;
import com.hospital.register.model.hosp.HospitalSet;
import com.hospital.register.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * 处理医院设置管理请求
 * @author littecheung
 */
@Api("医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院设置表所有信息
     * @return
     */
    @ApiOperation("获取所有医院信息")
    @GetMapping("findAll")
    public Result<List<HospitalSet>> findAllHospitalSet(){
        //调用service方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    /**
     * 逻辑删除医院设置
     * @param id
     * @return
     */
    @ApiOperation("逻辑删除医院信息")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    /**
     * 条件查询带分页
     * @param current 当前页
     * @param limit 每页记录数
     * @param hospitalSetQueryVo 条件对象
     * @return
     */
    @ApiOperation("条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result<Page<HospitalSet>> findPageHospSet(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建page对象进行分页
        Page<HospitalSet> page = new Page<>(current, limit);
        //构建拼接条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();

        //获取传过来的查询条件
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hoscode);
        }
        //调用方法实现分页查询
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        return Result.ok(hospitalSetPage);
    }

    /**
     * 添加医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态，1可使用，0不可使用
        hospitalSet.setStatus(1);
        //设置签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        boolean save = hospitalSetService.save(hospitalSet);

        return save ? Result.ok() : Result.fail();
    }

    /**
     * 根据id获取医院设置
     * @param id
     * @return
     */
    @ApiOperation("根据id获取医院设置")
    @GetMapping("getHospitalSet/{id}")
    public Result<HospitalSet> getHospitalSet(@PathVariable Long id){
        return Result.ok(hospitalSetService.getById(id));
    }


    /**
     * 修改医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation("修改医院设置")
    @PutMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean update = hospitalSetService.updateById(hospitalSet);
        return update ? Result.ok() : Result.fail();
    }

    /**
     * 批量删除医院设置
     * @param idList
     * @return
     */
    @ApiOperation("批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        boolean batch = hospitalSetService.removeByIds(idList);
        return batch ? Result.ok() : Result.fail();
    }


    /**
     * 医院设置锁定和解锁
     * (医院锁定后不能再上传数据)
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置医院状态
        hospitalSet.setStatus(status);
        //更新信息
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    /**
     * 发送签名密钥
     * (通过短信发送医院编号和签名key给医院方联系人，方便其对接接口)
     * @param id
     * @return
     */
    @ApiOperation("根据id发送签名密钥")
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id){

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }
}

