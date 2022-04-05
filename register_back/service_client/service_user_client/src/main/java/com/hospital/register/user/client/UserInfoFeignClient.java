package com.hospital.register.user.client;

import com.hospital.register.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 用户信息服务调用封装
 * TODO 需要实现服务降级熔断时，创建该接口实现类
 * @author littlecheung
 */
@Component
@FeignClient("service-user")
public interface UserInfoFeignClient {

    /**
     * 根据用户认证的姓名，模糊查询叫该用户的所用用户列表，用作远程调用
     * @param username
     * @return
     */
    @GetMapping("/admin/user/findUserListByUserName/{username}")
    public List<UserInfo> findUserListByUserName(@PathVariable("username") String username);


    /**
     * 根据用户id返回用户信息，用作远程调用接口
     * @param userId
     * @return
     */
    @GetMapping("/admin/user/findUserById/{userId}")
    public UserInfo findUserById(@PathVariable("userId") Long userId);
}
