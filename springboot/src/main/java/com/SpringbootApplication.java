package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com")
@MapperScan({
        "com.bill",
        "com.user.mapper"

})
public class SpringbootApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringbootApplication.class, args);
	}

}
