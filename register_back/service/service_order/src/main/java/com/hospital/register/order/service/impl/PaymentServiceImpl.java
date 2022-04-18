package com.hospital.register.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.register.common.exception.RegisterException;
import com.hospital.register.common.helper.HttpRequestHelper;
import com.hospital.register.common.result.ResultCodeEnum;
import com.hospital.register.enums.OrderStatusEnum;
import com.hospital.register.enums.PaymentStatusEnum;
import com.hospital.register.enums.PaymentTypeEnum;
import com.hospital.register.hosp.client.HospitalFeignClient;
import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.model.order.PaymentInfo;
import com.hospital.register.order.mapper.PaymentMapper;
import com.hospital.register.order.service.OrderService;
import com.hospital.register.order.service.PaymentService;
import com.hospital.register.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    /**
     * 向支付记录表中添加支付记录，保存交易记录
     * @param order 订单
     * @param paymentType 支付类型
     */
    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        //根据订单id和支付类型，查询支付记录表是否存在相同订单
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",order.getId());
        wrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0){
            //说明存在记录，直接返回，不用再存数据库
            return;
        }
        //添加交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        //subject为当前支付的内容信息
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());

        baseMapper.insert(paymentInfo);
    }

    /**
     * 支付成功后，更新支付记录和订单支付状态
     * @param outTradeNo 订单编号
     * @param resultMap 订单支付状态
     */
    @Override
    public void paySuccess(String outTradeNo, Map<String, String> resultMap) {

        //根据订单编号得到支付记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", outTradeNo);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);

        //更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        //根据订单号得到订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        //更新订单信息
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        //调用医院接口，更新订单支付信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new RegisterException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);

        //发送请求
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
        if(result.getInteger("code") != 200) {
            throw new RegisterException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
    }

    /**
     * 根据订单id、支付类型获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.eq("payment_type", paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }
}
