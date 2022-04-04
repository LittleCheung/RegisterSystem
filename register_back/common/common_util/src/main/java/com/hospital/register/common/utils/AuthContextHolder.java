package com.hospital.register.common.utils;

import com.hospital.register.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前用户信息工具类
 * @author littlecheung
 */

public class AuthContextHolder {

    /**
     * 获取当前用户id
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        //JWT从token获取userid
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }


    /**
     * 获取当前用户名称
     * @param request
     * @return
     */
    public static String getUserName(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        //JWT从token获取username
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
