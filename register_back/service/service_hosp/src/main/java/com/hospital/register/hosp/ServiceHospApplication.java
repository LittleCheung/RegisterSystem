package com.hospital.register.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 医院管理相关接口启动类
 * @author littlecheung
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.hospital")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hospital")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
