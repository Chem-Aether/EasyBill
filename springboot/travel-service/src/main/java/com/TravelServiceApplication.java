package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.travel.mapper")
public class TravelServiceApplication {
    public static void main(String[] args) { SpringApplication.run(TravelServiceApplication.class, args); }
}
