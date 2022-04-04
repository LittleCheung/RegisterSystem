package com.hospital.register.order.service;

import com.hospital.register.model.order.PaymentInfo;
import com.hospital.register.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * 根据支付记录，保存退款记录
     * @param paymentInfo
     * @return
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
