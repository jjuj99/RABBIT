package com.rabbit.auth.controller;

import com.rabbit.auth.controller.swagger.AuthControllerSwagger;
import com.rabbit.auth.controller.swagger.BankAuthControllerSwagger;
import com.rabbit.auth.domain.dto.request.AccountAuthSendRequestDTO;
import com.rabbit.auth.domain.dto.request.AccountAuthVerifyRequestDTO;
import com.rabbit.auth.domain.dto.response.AccountAuthVerifyResponseDTO;
import com.rabbit.auth.service.BankAuthService;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.user.domain.entity.Bank;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bank Auth", description = "계좌 검증 관련 API")
@RestController
@RequestMapping("/api/v1/bank/auth")
@RequiredArgsConstructor
@Validated
public class BankAuthController {

    private final BankAuthService bankAuthService;

    @BankAuthControllerSwagger.getAllBanksApi
    @GetMapping("/list")
    public ResponseEntity<CustomApiResponse<List<Bank>>> getAllBanks() {
        List<Bank> banks = bankAuthService.findAllBanks();

        return ResponseEntity.ok(CustomApiResponse.success(banks));
    }

    @BankAuthControllerSwagger.accountAuthSendApi
    @PostMapping("/refund-account/send")
    public ResponseEntity<CustomApiResponse<MessageResponse>> accountAuthSend(@RequestBody AccountAuthSendRequestDTO request) {
        bankAuthService.accountAuthSend(request);

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("해당 계좌로 인증 번호를 전송했습니다.")));
    }

    @BankAuthControllerSwagger.accountAuthVerifyApi
    @PostMapping("/refund-account/verify")
    public ResponseEntity<CustomApiResponse<AccountAuthVerifyResponseDTO>> accountAuthVerify(@RequestBody @Valid AccountAuthVerifyRequestDTO request) {
        AccountAuthVerifyResponseDTO response = bankAuthService.accountAuthVerify(request);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
