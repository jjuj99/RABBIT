package com.rabbit.global.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class TranslationController {

    @Autowired
    private MessageSource messageSource;

    // 언어별 번역 캐시 (성능 최적화)
    private final Map<Locale, Map<String, String>> translationCache = new ConcurrentHashMap<>();

    // 번역 키 목록 (한 번만 정의)
    private static final String[] TRANSLATION_KEYS = {
//            // 공통 메시지
//            "welcome", "logout",
//
//            // 로그인 관련
//            "login.title", "login.username", "login.password", "login.button",
//
//            // 에러 메시지
//            "error.login.failed", "error.required.field",

            // 여기에 추가 번역 키들 추가
            // ...
    };

    @GetMapping("/translations")
    public ResponseEntity<Map<String, String>> getTranslations() {
        // 현재 요청의 Locale 가져오기
        Locale locale = LocaleContextHolder.getLocale();

        // 캐시에서 번역 데이터 확인
        Map<String, String> translations = translationCache.get(locale);

        // 캐시에 없으면 새로 생성하여 캐시에 저장
        if (translations == null) {
            translations = loadTranslations(locale);
            translationCache.put(locale, translations);
        }

        return ResponseEntity.ok(translations);
    }

    /**
     * 주어진 로케일에 대한 모든 번역을 로드
     */
    private Map<String, String> loadTranslations(Locale locale) {
        Map<String, String> translations = new HashMap<>();

        for (String key : TRANSLATION_KEYS) {
            translations.put(key, getMessage(key, locale));
        }

        return translations;
    }

    /**
     * 메시지 소스에서 특정 키의 번역 가져오기
     */
    private String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, code, locale);
    }

    /**
     * 캐시 무효화 (관리자용 API 또는 내부 사용)
     */
    @GetMapping("/translations/refresh")
    public ResponseEntity<?> refreshTranslations() {
        translationCache.clear();
        return ResponseEntity.ok().build();
    }
}
