package com.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class RabbitApplication {
// 주석 추가
	public static void main(String[] args) {
		SpringApplication.run(RabbitApplication.class, args);
	}

}
