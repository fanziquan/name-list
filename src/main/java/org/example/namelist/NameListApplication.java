package org.example.namelist;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 启动类
 */
@SpringBootApplication
@MapperScan("org.example.namelist.mapper")
@EnableScheduling
public class NameListApplication {

    public static void main(String[] args) {
        SpringApplication.run(NameListApplication.class, args);
    }
}
