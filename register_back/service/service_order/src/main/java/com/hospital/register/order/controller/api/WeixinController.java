package com.hospital.register.order.controller.api;


import com.hospital.register.common.result.Result;
import com.hospital.register.order.service.PaymentService;
import com.hospital.register.order.service.WeixinService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 根据订单id生成微信二维码
     * @param orderId 订单id
     * @return
     */
    @ApiOperation("生成微信支付二维码接口")
    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }


    /**
     * 根据订单id查询订单支付状态接口
     * @param orderId 订单id
     * @return
     */
    @ApiOperation("查询支付状态接口")
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        //调用微信接口实现支付状态查询
        Map<String, String> resultMap =  weixinService.queryPayStatus(orderId);
        //对支付状态进行条件判断
        if(resultMap == null){
            return Result.fail().message("支付出错");
        }
        if("SUCCESS".equals(resultMap.get("trade_state"))){
            //支付成功，更新订单状态，outTradeNo为订单编码
            String outTradeNo = resultMap.get("out_trade_no");
            paymentService.paySuccess(outTradeNo, resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }
}
