package com.hospital.register.common.handler;

import com.hospital.register.common.exception.RegisterException;
import com.hospital.register.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 * @author littlecheung
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 其他异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(RegisterException.class)
    @ResponseBody
    public Result error(RegisterException e){
        e.printStackTrace();
        return Result.build(e.getCode(), e.getMessage());
    }
}
