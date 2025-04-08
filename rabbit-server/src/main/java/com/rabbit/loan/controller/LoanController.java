package com.rabbit.loan.controller;

import com.rabbit.global.request.PageRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.loan.domain.dto.response.*;
import com.rabbit.loan.service.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CustomApiResponse<BorrowSummaryResponseDTO>> borrowSummary() { // Authentication authentication
//        String userId = (String) authentication.getPrincipal();
        String userId = "1";
        BorrowSummaryResponseDTO response = loanService.borrowSummary(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/borrow/me")
    public ResponseEntity<CustomApiResponse<PageResponseDTO<BorrowListResponseDTO>>> borrowList(@ModelAttribute PageRequestDTO request, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        Pageable pageable = request.toPageable("contractId", Sort.Direction.DESC);
        PageResponseDTO<BorrowListResponseDTO> response = loanService.borrowList(Integer.parseInt(userId), pageable);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/borrow/{contractId}")
    public ResponseEntity<CustomApiResponse<BorrowDetailResponseDTO>> borrowDetail(@PathVariable int contractId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        BorrowDetailResponseDTO response = loanService.borrowDetail(contractId, Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/lent/me/summary")
    public ResponseEntity<CustomApiResponse<LentSummaryResponseDTO>> lentSummary(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        LentSummaryResponseDTO response = loanService.lentSummary(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/lent/me")
    public ResponseEntity<CustomApiResponse<PageResponseDTO<LentListResponseDTO>>> lentList(@ModelAttribute PageRequestDTO request, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        Pageable pageable = request.toPageable("contractId", Sort.Direction.DESC);
        PageResponseDTO<LentListResponseDTO> response = loanService.lentList(Integer.parseInt(userId), pageable);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/lent/{contractId}")
    public ResponseEntity<CustomApiResponse<LentDetailResponseDTO>> lentDetail(@PathVariable int contractId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        LentDetailResponseDTO response = loanService.lentDetail(contractId, Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @GetMapping("/lent/available-auctions")
    public ResponseEntity<CustomApiResponse<?>> getAuctionAvailable(@Valid PageRequestDTO pageRequest, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        Pageable pageable = pageRequest.toPageable();
        PageResponseDTO<LentAuctionResponseDTO> response = loanService.getAuctionAvailable(Integer.parseInt(userId), pageable);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
