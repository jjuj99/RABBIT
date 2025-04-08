package com.rabbit.promissorynote.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.promissorynote.domain.dto.request.PrepaymentRequestDTO;
import com.rabbit.promissorynote.service.PNService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promissory Note", description = "차용증 관련 API")
@RestController
@RequestMapping("/api/v1/promissory-notes")
@RequiredArgsConstructor
@Validated
public class PromissorynoteController {

    private final PNService pnService;

    @PostMapping("/debts/{contractId}/prepayment")
    public ResponseEntity<CustomApiResponse<MessageResponse>> prepayment(@RequestBody PrepaymentRequestDTO request,
                                                                         @PathVariable Integer contractId,
                                                                         Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        pnService.prepayment(request, contractId, Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("중도상환이 되었습니다.")));
    }
}
