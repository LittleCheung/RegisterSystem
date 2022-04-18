package com.hospital.register.order.service;

import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.vo.order.OrderCountQueryVo;
import com.hospital.register.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface OrderService extends IService<OrderInfo> {

    /**
     * 根据scheduleId patientId生成挂号订单接口
     * @param scheduleId
     * @param patientId
     * @return
     */
    Long saveOrder(String scheduleId, Long patientId);

    /**
     * 根据订单id查询订单详情
     * @param orderId
     * @return
     */
    OrderInfo getOrder(Long orderId);

    /**
     * 查询订单列表并进行分页
     * @param pageParam
     * @param orderQueryVo
     * @return
     */
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 根据orderId 取消预约接口
     * @param orderId
     * @return
     */
    Boolean cancelOrder(Long orderId);

    /**
     * 发送消息提醒就诊人
     */
    void patientTips();

    /**
     * 预约统计
     * @param orderCountQueryVo
     * @return
     */
    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

    /**
     * 获取分页列表
     * @param pageParam
     * @param orderQueryVo
     * @return
     */
    IPage<OrderInfo> selectAdminPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
}
