package com.hospital.register.order.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface WeixinService {
    /**
     * 根据orderId生成微信二维码
     * @param orderId
     * @return
     */
    Map createNative(Long orderId);

    /**
     * 根据订单id 查询订单状态
     * @param orderId
     * @return
     */
    Map<String, String> queryPayStatus(Long orderId);

    /**
     * 根据订单id 进行微信退款操作
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);
}
