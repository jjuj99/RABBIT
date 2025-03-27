package com.rabbit.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "Test 관련 API")
@RestController
@RequestMapping("/api/v1/auth/test")
@RequiredArgsConstructor
public class HelloController {

    @GetMapping
    public String test() {
        return "Test Jenkins! !";
    }

    @GetMapping("/hoot")
    public String hook() {
        return "Test Web hook! !";
    }

    @GetMapping("/push/main")
    public String push() { return "Push Main hook!"; }
}
