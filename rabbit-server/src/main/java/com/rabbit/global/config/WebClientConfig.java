package com.rabbit.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    public static final String SSAFY_BANK_BASE_URL = "https://finopenapi.ssafy.io/ssafy/api/v1";

    @Bean
    public WebClient webClient() {
        // Netty 기반 비동기 HttpClient 생성 및 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃 5초
                .responseTimeout(Duration.ofSeconds(5)) // 응답 대기 최대 5초
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS)) // 읽기 타임아웃 5초
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))); // 쓰기 타임아웃 5초

        // WebClient Bean 생성 및 커스터마이징
        return WebClient.builder()
                .baseUrl(SSAFY_BANK_BASE_URL) // 기본 호출 주소 설정
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // JSON 요청 헤더
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) // JSON 응답 헤더
                .clientConnector(new ReactorClientHttpConnector(httpClient)) // Netty 기반 HttpClient 연결
                .filter(logRequest()) // 요청 로깅 필터 추가
                .filter(logResponse()) // 응답 로깅 필터 추가
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("➡️ [WebClient Request] {} {}", request.method(), request.url());

            request.headers().forEach((name, values) ->
                    values.forEach(value -> log.debug("➡️ Header: {} = {}", name, value)));

            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("⬅️ [WebClient Response] Status: {}", response.statusCode());

            // 성공 상태 코드인 경우
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(String.class)
                        .doOnNext(body -> log.info("⬅️ Success Response Body: {}", body))
                        .map(body -> response.mutate().body(body).build());
            }

            // 에러 상태 코드인 경우 로깅 후 에러 정보 추출
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.error("⬅️ Error Response Body: {}", body);
                        return Mono.error(new WebClientResponseException(
                                response.statusCode().value(),
                                response.statusCode().toString(),
                                response.headers().asHttpHeaders(),
                                body.getBytes(),
                                null));
                    });
        });
    }
}
