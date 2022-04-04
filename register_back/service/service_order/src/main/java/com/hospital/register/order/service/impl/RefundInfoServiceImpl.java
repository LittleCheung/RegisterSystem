package com.hospital.register.order.service.impl;

import com.hospital.register.enums.RefundStatusEnum;
import com.hospital.register.model.order.PaymentInfo;
import com.hospital.register.model.order.RefundInfo;
import com.hospital.register.order.mapper.RefundInfoMapper;
import com.hospital.register.order.service.RefundInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    /**
     * 根据支付记录，保存退款记录
     * @param paymentInfo 支付记录
     * @return 退款记录
     */
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        //判断是否有重复数据添加
        QueryWrapper<RefundInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", paymentInfo.getOrderId());
        wrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(wrapper);
        if(refundInfo !=null){
            //如果数据存在直接返回
            return refundInfo;
        }
        //添加退款记录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        //退款状态
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());

        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
