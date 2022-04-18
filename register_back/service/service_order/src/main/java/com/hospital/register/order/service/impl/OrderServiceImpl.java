package com.hospital.register.order.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.hospital.common.rabbit.constant.MqConst;
import com.hospital.common.rabbit.service.RabbitService;
import com.hospital.register.common.exception.RegisterException;
import com.hospital.register.common.helper.HttpRequestHelper;
import com.hospital.register.common.result.ResultCodeEnum;
import com.hospital.register.enums.OrderStatusEnum;
import com.hospital.register.hosp.client.HospitalFeignClient;
import com.hospital.register.model.hosp.Schedule;
import com.hospital.register.model.order.OrderInfo;
import com.hospital.register.model.user.Patient;
import com.hospital.register.model.user.UserInfo;
import com.hospital.register.order.mapper.OrderMapper;
import com.hospital.register.order.repository.ScheduleRepository;
import com.hospital.register.order.service.OrderService;
import com.hospital.register.order.service.WeixinService;
import com.hospital.register.user.client.PatientFeignClient;
import com.hospital.register.user.client.UserInfoFeignClient;
import com.hospital.register.vo.hosp.ScheduleOrderVo;
import com.hospital.register.vo.msm.MsmVo;
import com.hospital.register.vo.order.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,OrderInfo> implements OrderService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 根据排班id和就诊人id生成挂号订单接口
     * @param scheduleId 排班id
     * @param patientId 就诊人id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //TODO 该方法代码过于臃肿，可以考虑提取出部分代码进行调用
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        if(null == patient) {
            throw new RegisterException(ResultCodeEnum.PARAM_ERROR);
        }
        //获取排班相关信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        if(null == scheduleOrderVo) {
            throw new RegisterException(ResultCodeEnum.PARAM_ERROR);
        }
        //判断当前时间是否还可以预约：当前时间不可以预约
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new RegisterException(ResultCodeEnum.TIME_NO);
        }
        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
        //添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //向orderInfo设置其他数据
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口,实现预约挂号操作，设置医院接口需要的参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        /**
         * 根据前端传过来的scheduleId查询hosScheduleId
         * 由于该项目数据存储设计不合理，这里存在mongodb中的主键id与MySQL数据库表中的id不一致
         */
        Schedule scheduleById = scheduleRepository.findScheduleById(scheduleId);
        String hosScheduleIdTemp = null;
        if(scheduleById!=null){
            hosScheduleIdTemp = scheduleById.getHosScheduleId();
        }
        Long hosScheduleId = null;
        try {
            hosScheduleId = Long.parseLong(hosScheduleIdTemp);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //就诊人基本信息
        paramMap.put("hosScheduleId",hosScheduleId);
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人信息
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);
        //请求医院系统接口
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl()+"/order/submitOrder");
        //请求成功，获取数据
        if(result.getInteger("code") == 200){
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            //更新数据库
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");

            //发送mq信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            //使用mq发送信息：参数为交换机、路由键、排班更新实体
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        }else{
            throw new RegisterException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        //返回订单id
        return orderInfo.getId();
    }

    /**
     * 根据订单id查询订单详情信息
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }

    /**
     * 条件查询订单列表并进行分页
     * @param pageParam
     * @param orderQueryVo
     * @return
     */
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //医院名称
        String keyword = orderQueryVo.getKeyword();
        //就诊人id
        Long patientId = orderQueryVo.getPatientId();
        //订单状态
        String orderStatus = orderQueryVo.getOrderStatus();
        //安排时间
        String reserveDate = orderQueryVo.getReserveDate();
        //创建开始时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        //创建结束时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)){
            wrapper.like("hosname", keyword);
        }
        if(!StringUtils.isEmpty(patientId)){
            wrapper.like("patient_id", patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            wrapper.like("order_status", orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            wrapper.like("reserve_date", reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge("create_timeBegin", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le("create_timeBegin", createTimeEnd);
        }
        //排序
        wrapper.orderByDesc("update_time");
        //进行查询
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值的封装
        pages.getRecords().stream().forEach(item ->{
            this.packOrderInfo(item);
        });
        return pages;
    }

    /**
     * 根据订单id取消预约接口
     * @param orderId 订单id
     * @return
     */
    @Transactional
    @Override
    public Boolean cancelOrder(Long orderId) {
        //获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //判断是否可以取消，项目中规定下午3：30前可以取消
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()){
            //超过当前时间，不能取消
            throw new RegisterException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //调用医院模拟系统进行取消预约
        //查询签名
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

        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updateCancelStatus");

        //根据医院接口返回数据
        if(result.getInteger("code") != 200){
            //返回失败！
            throw new RegisterException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }else {
            //返回成功
            //判断当前订单是否已经支付了
            if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                //已支付
                Boolean isRefund = weixinService.refund(orderId);
                if (!isRefund) {
                    //退款失败
                    throw new RegisterException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
            }
            //无论是不是已经支付的，都进行更新订单
            //更新订单状态
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            baseMapper.updateById(orderInfo);
            //发送mq 更新预约数量
            //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("SMS_194640722");
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        }
        return true;
    }

    /**
     * mq使用方法：发送短信提醒就诊人
     */
    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        //对所有该时间段内的客户发送短信
        for (OrderInfo orderInfo : orderInfoList) {
            //短信提示固定模板
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }

    /**
     * 获取医院每天平台预约数据（用于预约统计图表）
     * @param orderCountQueryVo 订单统计对象
     * @return
     */
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //调用mapper得到数据
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        //获取x轴，日期数据
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //获取y轴，具体数量
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }

    /**
     * 获取订单分页列表
     * @param pageParam
     * @param orderQueryVo
     * @return
     */
    @Override
    public IPage<OrderInfo> selectAdminPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();

        //根据就诊人姓名模糊查询
        String patientName = orderQueryVo.getPatientName();
        if(!StringUtils.isEmpty(patientName)){
            wrapper.like("patient_name", patientName);
        }

        //根据用户姓名模糊查询
        String userName = orderQueryVo.getUserName();
        if(!StringUtils.isEmpty(userName)){
            //远程调用，获取用户列表
            List<UserInfo> userInfoList = userInfoFeignClient.findUserListByUserName(userName);

            List<Long> userNameList = new ArrayList<>();

            if (userInfoList!=null && userInfoList.size()!=0){
                //如果查询有响应用户，则对这些id进行查询
                for (UserInfo userInfo : userInfoList) {
                    userNameList.add(userInfo.getId());
                }
            }else{
                //该用户名没有用户，那么就让wraper查询用户id为-1的用户
                userNameList.add(-1L);
            }
            wrapper.in("user_id",userNameList);
        }

        //根据订单状态查询
        String orderStatus = orderQueryVo.getOrderStatus();
        if(!StringUtils.isEmpty(orderStatus)){
            wrapper.like("order_status", orderStatus);
        }

        wrapper.orderByDesc("update_time");
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值的封装
        pages.getRecords().stream().forEach(item ->{
            Long userId = item.getUserId();//订单会员名称
            item.getParam().put("userName",userInfoFeignClient.findUserById(userId).getName());  //封装订单会员名称
            this.packOrderInfo(item); //订单状态
        });
        return pages;
    }


    /**
     * 对订单信息进行封装进行封装
     * @param orderInfo 订单信息
     * @return
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

}



