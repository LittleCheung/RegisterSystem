package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 医院接口模拟管理系统启动类，用于去调用预约挂号平台相关接口
 * @author littlecheung
 *
 * “@EnableTransactionManagement”：表示加入事务
 */
@SpringBootApplication
@EnableTransactionManagement
public class ManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageApplication.class, args);
	}

}
