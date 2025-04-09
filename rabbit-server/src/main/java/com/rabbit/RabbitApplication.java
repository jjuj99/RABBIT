package com.rabbit;

import com.rabbit.global.config.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableConfigurationProperties(CorsProperties.class)
public class RabbitApplication {
// 트리거
	public static void main(String[] args) { SpringApplication.run(RabbitApplication.class, args); }

}
