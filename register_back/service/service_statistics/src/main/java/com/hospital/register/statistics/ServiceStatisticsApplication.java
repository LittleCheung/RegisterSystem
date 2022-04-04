package com.hospital.register.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 统计模块启动类
 * @author littlecheung
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hospital")
@ComponentScan(basePackages = {"com.hospital"})
public class ServiceStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class, args);
    }

}
