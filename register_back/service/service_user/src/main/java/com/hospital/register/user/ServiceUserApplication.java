package com.hospital.register.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 登录注册模块启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.hospital")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hospital")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
