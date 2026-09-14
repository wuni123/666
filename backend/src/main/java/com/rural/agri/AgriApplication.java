package com.rural.agri;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rural.agri.mapper")
public class AgriApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriApplication.class, args);
    }
}
