package com.hospital.register.order.service;

import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    /**
     * 向支付记录表中添加支付记录
     * @param order
     * @param paymentType
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);

    /**
     * 支付成功后，更新支付记录、订单状态
     * @param out_trade_no
     * @param resultMap
     */
    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    /**
     * 根据订单id、支付类型获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
