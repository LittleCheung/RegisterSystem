package com.hospital.register.msm.controller;


import com.hospital.register.common.result.Result;
import com.hospital.register.msm.service.MsmService;
import com.hospital.register.msm.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发送手机验证码的接口
     * @param phone 手机号
     * @return
     */
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){

        //从redis里面获取，key为手机号，value为验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)){
            //成功获取返回结果
            return Result.ok();
        }
        //如果从redis获取不到则生成六位验证码
        code = RandomUtil.getSixBitRandom();
        //整合阿里云短信服务进行发送，
        boolean isSend = msmService.send(phone, code);
        //生成的验证码放到redis中，并设置有效时间
        if(isSend){
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        }else{
            return Result.fail().message("发送短信失败");
        }

    }
}
