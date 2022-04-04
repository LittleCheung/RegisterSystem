package com.hospital.register.vo.order;

import com.hospital.register.vo.msm.MsmVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "OrderMqVo")
public class OrderMqVo {

	@ApiModelProperty(value = "可预约数")
	private Integer reservedNumber;

	@ApiModelProperty(value = "剩余预约数")
	private Integer availableNumber;

	@ApiModelProperty(value = "排班id")
	private String scheduleId;

	@ApiModelProperty(value = "短信实体")
	private MsmVo msmVo;

}

