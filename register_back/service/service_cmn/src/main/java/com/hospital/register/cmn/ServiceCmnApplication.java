package com.hospital.register.cmn;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 数据字典管理启动类
 * @author littlecheung
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.hospital")
@EnableDiscoveryClient
public class ServiceCmnApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }

}
