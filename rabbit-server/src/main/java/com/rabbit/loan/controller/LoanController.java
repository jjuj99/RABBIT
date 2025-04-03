package com.rabbit.loan.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import com.rabbit.loan.service.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Loan", description = "나의 채무, 채권 정보 관련 API")
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Validated
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/borrow/me/summary")
    public ResponseEntity<CustomApiResponse<BorrowSummaryResponseDTO>> borrowSummary(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        BorrowSummaryResponseDTO response = loanService.borrowSummary(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

}
