package com.hospital.register.common.exception;

import com.hospital.register.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author qy
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class RegisterException extends RuntimeException {

    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public RegisterException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public RegisterException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "RegisterException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
