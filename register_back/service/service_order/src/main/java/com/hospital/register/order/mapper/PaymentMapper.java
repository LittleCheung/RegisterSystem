package com.hospital.register.order.mapper;

import com.hospital.register.model.order.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<PaymentInfo> {
}
