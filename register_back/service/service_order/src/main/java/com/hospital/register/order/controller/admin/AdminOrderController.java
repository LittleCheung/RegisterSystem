package com.hospital.register.order.controller.admin;

import com.hospital.register.common.result.Result;
import com.hospital.register.enums.OrderStatusEnum;
import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.order.service.OrderService;
import com.hospital.register.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("后台订单接口")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 返回订单状态
     * @return
     */
    @ApiOperation("获取订单状态")
    @GetMapping("getStatusList")
    public Result getStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    /**
     * 根据 订单会员人、就诊人、订单状态等信息，查询订单列表，并进行分页
     * @param page
     * @param limit
     * @param orderQueryVo
     * @return
     */
    @ApiOperation("查询订单列表并分页")
    @PostMapping("findOrderList/{page}/{limit}")
    public Result findOrderList(@PathVariable Long page, @PathVariable Long limit,
                                OrderQueryVo orderQueryVo){
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectAdminPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }


    /**
     * 后台取消预约接口
     * @param orderId
     * @return
     */
    @ApiOperation("取消预约接口")
    @GetMapping("cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        Boolean aBoolean = orderService.cancelOrder(orderId);
        return Result.ok(aBoolean);
    }

    /**
     * 根据订单 id 查询订单详情
     * @param orderId
     * @return
     */
    @ApiOperation("根据订单id查询订单详情")
    @GetMapping("getOrder/{orderId}")
    public Result getOrder(@PathVariable Long orderId){
        OrderInfo orderInfo  = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

}
