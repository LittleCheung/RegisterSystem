package com.hospital.register.user.service;


import com.hospital.register.model.user.UserInfo;
import com.hospital.register.vo.user.LoginVo;
import com.hospital.register.vo.user.UserAuthVo;
import com.hospital.register.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    /**
     * 用户进行登录
     * @param loginVo
     * @return
     */
    Map<String, Object> loginUser(LoginVo loginVo);

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    UserInfo selectWxInfoOpenId(String openid);

    /**
     * 用户根据id, userAuthvo进行认证
     * @param userId
     * @param userAuthVo
     */
    void userAuth(Long userId, UserAuthVo userAuthVo);

    /**
     * 用户列表（条件加分页）
     * @param pageParm
     * @param userInfoQueryVo
     * @return
     */
    IPage<UserInfo> selectPage(Page<UserInfo> pageParm, UserInfoQueryVo userInfoQueryVo);

    /**
     * 更改用户锁定状态
     * @param userId
     * @param status
     */
    void lock(Long userId, Integer status);

    /**
     * 展示某用户详情接口
     * @param userId
     * @return
     */
    Map<String, Object> show(Long userId);

    /**
     * 用户认证审批
     * @param userId
     * @param authStatus
     */
    void approve(Long userId, Integer authStatus);

    /**
     * 根据用户认证的姓名，模糊查询叫该用户的所用用户列表
     * @param username
     * @return
     */
    List<UserInfo> findUserListByUserName(String username);
}
