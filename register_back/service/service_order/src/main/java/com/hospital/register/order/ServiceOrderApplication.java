package com.hospital.register.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 预约挂号模块启动类
 * @author littlecheung
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.hospital"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.hospital"})
@EnableTransactionManagement
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
