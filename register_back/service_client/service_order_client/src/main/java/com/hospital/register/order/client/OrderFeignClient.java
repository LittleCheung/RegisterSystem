package com.hospital.register.order.client;

import com.hospital.register.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
@FeignClient("service-order")
public interface OrderFeignClient {

    /**
     * 获取订单统计数据接口
     * @param orderCountQueryVo
     * @return
     */
    @ApiOperation("获取订单统计数据")
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    public Map<String,Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);


}
