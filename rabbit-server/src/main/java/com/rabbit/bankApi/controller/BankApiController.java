package com.rabbit.bankApi.controller;

import com.rabbit.bankApi.domain.dto.request.*;
import com.rabbit.bankApi.domain.dto.response.*;
import com.rabbit.bankApi.service.BankApiService;
import com.rabbit.global.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@Hidden
@Tag(name = "Bank", description = "SSAFY 은행 기능 관련 API")
@RestController
@RequestMapping("/api/v1/bank")
@RequiredArgsConstructor
@Validated
public class BankApiController {

    private final BankApiService bankApiService;

    @Operation(summary = "api key 발급")
    @GetMapping("/issue-apikey")
    public ResponseEntity<CustomApiResponse<ApiKeyResponseDTO>> issueApiKey(@RequestParam String email) {
        ApiKeyResponseDTO response = bankApiService.issueApiKey(email).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Operation(summary = "api key 재발급")
    @GetMapping("/re-issue-apikey")
    public ResponseEntity<CustomApiResponse<ApiKeyResponseDTO>> reIssueApiLKey(@RequestParam String email) {
        ApiKeyResponseDTO response = bankApiService.reIssueApiKey(email).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Operation(summary = "사용자 생성")
    @PostMapping("/member")
    public ResponseEntity<CustomApiResponse<MemberResponseDTO>> createMember(@RequestBody MemberRequestDTO request) {
        MemberResponseDTO response = bankApiService.createMember(request).block();

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(response));
    }

    @Operation(summary = "사용자 조회")
    @GetMapping("/member")
    public ResponseEntity<CustomApiResponse<MemberResponseDTO>> searchMember(MemberRequestDTO request) {
        MemberResponseDTO response = bankApiService.searchMember(request).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Operation(summary = "계좌 상품 등록")
    @PostMapping("/create/demand-deposit")
    public ResponseEntity<CustomApiResponse<CreateDemandDepositResponseDTO>> createDemandDeposit(@RequestBody CreateDemandDepositRequestDTO request) {
        CreateDemandDepositResponseDTO response = bankApiService.createDemandDeposit(request).block();

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(response));
    }

    @Operation(summary = "계좌 개설")
    @PostMapping("/craete/demand-account")
    public ResponseEntity<CustomApiResponse<CreateDemandAccountResponseDTO>> createDemandAccount(@RequestBody CreateDemandAccountRequestDTO request) {
        CreateDemandAccountResponseDTO response = bankApiService.createDemandAccount(request).block();

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(response));
    }

    @Operation(summary = "계좌 거래 내역 조회")
    @PostMapping("/inquire/transaction-history")
    public ResponseEntity<CustomApiResponse<InquireTransactionResponseDTO>> inquireTransaction(@RequestBody InquireTransactionRequestDTO request) {
        InquireTransactionResponseDTO response = bankApiService.inquireTransaction(request).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Operation(summary = "계좌로 입금")
    @PostMapping("/update/account-deposit")
    public ResponseEntity<CustomApiResponse<UpdateDepositResponseDTO>> updateDeposit(@RequestBody UpdateDepositRequestDTO request) {
        UpdateDepositResponseDTO response = bankApiService.updateDeposit(request).block();

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(response));
    }

    @Operation(summary = "내 신용 점수 조회")
    @PostMapping("/inquire/my-credit")
    public ResponseEntity<CustomApiResponse<MyCreditResponseDTO>> myCredit(@RequestBody UserKeyRequestDTO request) {
        MyCreditResponseDTO response = bankApiService.myCredit(request).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @Operation(summary = "1원 인증 요청")
    @PostMapping("/auth/open-account")
    public ResponseEntity<CustomApiResponse<OpenAccountResponseDTO>> openAccount(@RequestBody OpenAccountRequestDTO request) {
        OpenAccountResponseDTO response = bankApiService.openAccount(request).block();

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(response));
    }

    @Operation(summary = "1원 인증 검증")
    @PostMapping("/auth/check-account")
    public ResponseEntity<CustomApiResponse<CheckAccountResponseDTO>> checkAccount(@RequestBody CheckAccountRequestDTO request) {
        CheckAccountResponseDTO response = bankApiService.checkAccount(request).block();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
