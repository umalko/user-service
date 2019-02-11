package com.mavs.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@EnableEurekaClient
@SpringBootApplication
@ComponentScan({"com.mavs.activity.provider.*", "com.mavs.userservice.*"})
@EntityScan("com.mavs.userservice.*")
@EnableJpaRepositories("com.mavs.userservice.*")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
