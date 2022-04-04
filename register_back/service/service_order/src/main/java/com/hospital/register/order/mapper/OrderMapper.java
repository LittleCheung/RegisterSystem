package com.hospital.register.order.mapper;

import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.vo.order.OrderCountQueryVo;
import com.hospital.register.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
