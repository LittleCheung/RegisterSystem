package com.hospital.register.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * HospitalSet
 * </p>
 *
 * @author qy
 */
@Data
@ApiModel(description = "签名信息")
public class SignInfoVo  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "api基础路径")
	private String apiUrl;

	@ApiModelProperty(value = "签名秘钥")
	private String signKey;

}

