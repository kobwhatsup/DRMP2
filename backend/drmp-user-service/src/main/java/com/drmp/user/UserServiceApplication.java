package com.drmp.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户服务启动类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.drmp.common", "com.drmp.user"})
@EnableJpaAuditing
@EnableTransactionManagement
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}