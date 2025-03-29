package com.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class RabbitApplication {
// 트리거를 위한 의미없는 주석
	public static void main(String[] args) {
		SpringApplication.run(RabbitApplication.class, args);
	}

}
