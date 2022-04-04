package com.hospital.register.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 登录注册模块配置类
 * @author littlecheung
 */
@Configuration
@MapperScan("com.hospital.register.user.mapper")
public class UserConfig {

}
