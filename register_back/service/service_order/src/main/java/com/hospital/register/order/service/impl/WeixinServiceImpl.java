package com.hospital.register.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.register.enums.PaymentTypeEnum;
import com.hospital.register.enums.RefundStatusEnum;
import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.model.order.PaymentInfo;
import com.hospital.register.model.order.RefundInfo;
import com.hospital.register.order.service.OrderService;
import com.hospital.register.order.service.PaymentService;
import com.hospital.register.order.service.RefundInfoService;
import com.hospital.register.order.service.WeixinService;
import com.hospital.register.order.utils.ConstantPropertiesUtils;
import com.hospital.register.order.utils.HttpClient;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    /**
     * 根据订单id生成微信二维码
     * @param orderId 订单id
     * @return
     */
    @Override
    public Map createNative(Long orderId) {

        try {
            //从redis中获取数据
            Map payMap  = (Map) redisTemplate.opsForValue().get(orderId.toString());
            if(payMap !=null){
                return payMap;
            }

            //根据orderId获取订单信息
            OrderInfo order = orderService.getById(orderId);
            //向支付记录表里添加信息  第二个参数为支付类型
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());

            //设置参数，调用微信生成二维码接口V2，把参数转换成xml格式，使用商户key进行加密
            // TODO 后续可以更新为微信V3接口使用json格式
            Map paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = order.getReserveDate() + "就诊"+ order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            paramMap.put("total_fee", "1");
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            //微信支付类型
            paramMap.put("trade_type", "NATIVE");

            //HTTPClient来根据URL访问微信支付第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置map参数并完成发送
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //微信端返回相关数据
            String xml = client.getContent();
            //将xml转换成map集合
            Map<String, String>  resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("resultMap :" + resultMap);

            //封装返回结果集
            Map map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            //codeUrl是二维码地址
            map.put("codeUrl", resultMap.get("code_url"));

            //把返回结果存到redis：设置参数为key、value和过期时间
            if(resultMap.get("result_code")!=null){
                redisTemplate.opsForValue().set(orderId.toString(), map, 2, TimeUnit.MINUTES);
            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据订单id查询订单支付状态
     * @param orderId 订单id
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            //根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);

            //封装提交参数：微信支付id、商户号、订单编号、随机字符串
            Map paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            //HTTPClient来根据URL访问微信支付第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            //得到微信接口返回数据,由xml转换为map
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            System.out.println("支付状态 - resultMap：" + resultMap);
            //把接口数据返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据订单id进行微信退款操作
     * @param orderId 订单id
     * @return
     */
    @Override
    public Boolean refund(Long orderId) {
        try {
            //获取支付记录信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
            //添加信息到退款记录表
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            //判断当前订单数据是否已经退款
            if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()){
                //已经成功退款
                return true;
            }
            //调用微信接口实现退款
            //封装需要的参数：公众账号ID、商户编号、随机码、微信订单号、商户订单编号、商户退款单号
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo());
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo());
            paramMap.put("out_refund_no","tk" +paymentInfo.getOutTradeNo());
            //生成环境退款金额决定于挂号金额，测试可以使用0.01
            paramMap.put("total_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("refund_fee",paymentInfo.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            String paramXml = WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY);

            //设置调用接口内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);

            //设置退款证书信息
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();

            //接收返回数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if(resultMap != null && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))){
                //返回成功，设置refundInfo存入退款记录表
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            //退款失败
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
