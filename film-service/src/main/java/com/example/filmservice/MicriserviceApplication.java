package com.example.filmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient  // Подключаем Eureka
public class MicriserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicriserviceApplication.class, args);
    }
}
