package com.rabbit.example.controller;

import com.rabbit.example.controller.swagger.ExampleControllerSwagger;
import com.rabbit.example.domain.dto.request.ExampleRequestDTO;
import com.rabbit.example.domain.dto.request.ExampleSearchDTO;
import com.rabbit.example.domain.dto.request.ExampleSearchRequestDTO;
import com.rabbit.example.domain.dto.response.ExampleResponseDTO;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.request.PageRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.global.response.PageResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Slf4j
public class ExampleController {

    @ExampleControllerSwagger.InsertExampleApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<ExampleResponseDTO>> insertExample(
            @Valid @RequestBody ExampleRequestDTO request) {

        log.debug("[예시 생성 요청] request: {}", request);

        // 입력값 검증 (실제 @Valid가 처리하는 부분)
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.valueOf("INVALID_INPUT_VALUE"),
                    "제목은 필수 입력값입니다"
            );
        }

        // 가상의 새 데이터 생성 로직
        ExampleResponseDTO createdExample = ExampleResponseDTO.builder()
                .id(5L) // 새로 생성된 ID
                .title(request.getTitle())
                .content(request.getContent())
                .categoryCode(request.getCategoryCode())
                .isActive(request.getIsActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 생성된 리소스의 URI 생성
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdExample.getId())
                .toUri();

        // 201 Created 상태 코드와 함께 생성된 리소스 반환
        return ResponseEntity
                .created(location)
                .body(CustomApiResponse.success(createdExample));
    }

    @ExampleControllerSwagger.RemoveExampleApi
    @DeleteMapping("/{example-id}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> removeExample(
            @ExampleControllerSwagger.ExampleIdParam
            @PathVariable("example-id") Long exampleId) {

        log.debug("[예시 삭제 요청] exampleId: {}", exampleId);

        // 예시: 특정 ID에 대한 검증 로직
        if (exampleId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
        }

        // 예시: 존재하지 않는 데이터 처리
        if (exampleId > 100) {
            throw new BusinessException(
                    ErrorCode.valueOf("RESOURCE_NOT_FOUND"),
                    "존재하지 않는 예시 항목입니다"
            );
        }

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("삭제 성공")));
    }

    @ExampleControllerSwagger.UpdateExampleApi
    @PutMapping("/{example-id}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> updateExample(
            @ExampleControllerSwagger.ExampleIdParam
            @PathVariable("example-id") Long exampleId,

            @ExampleControllerSwagger.RequestBodyParam
            @Valid @RequestBody ExampleRequestDTO request) {

        log.debug("[예시 수정 요청] exampleId: {}, request: {}", exampleId, request);

        // 예시: 특정 ID에 대한 검증 로직
        if (exampleId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
        }

        // 예시: 존재하지 않는 데이터 처리
        if (exampleId > 100) {
            throw new BusinessException(
                    ErrorCode.valueOf("RESOURCE_NOT_FOUND"),
                    "존재하지 않는 예시 항목입니다"
            );
        }

        // 요청 본문 검증 예시 (실제로는 @Valid 어노테이션이 이미 처리해줌)
        if (request == null) {
            throw new BusinessException(
                    ErrorCode.valueOf("INVALID_INPUT_VALUE"),
                    "필수 파라미터가 누락되었습니다"
            );
        }

        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("수정 성공")));
    }

    @ExampleControllerSwagger.SelectExampleApi
    @GetMapping("/{example-id}")
    public ResponseEntity<CustomApiResponse<ExampleResponseDTO>> selectExample(
            @ExampleControllerSwagger.ExampleIdParam
            @PathVariable("example-id") Long exampleId) {

        log.debug("[예시 조회 요청] exampleId: {}", exampleId);

        // 예시: 특정 ID에 대한 검증 로직
        if (exampleId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE);
        }

        // 예시: 존재하지 않는 데이터 처리
        if (exampleId > 100) {
            throw new BusinessException(
                    ErrorCode.valueOf("RESOURCE_NOT_FOUND"),
                    "존재하지 않는 예시 항목입니다"
            );
        }

        // 실제 응답 예시 - 모든 필드가 채워진 응답
        ExampleResponseDTO response = ExampleResponseDTO.builder()
                .id(exampleId)
                .title("예시 제목입니다")
                .content("예시 내용입니다")
                .categoryCode("CATEGORY_A")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @ExampleControllerSwagger.SelectExampleListApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<PageResponseDTO<ExampleResponseDTO>>> selectExampleList(
            @Parameter(hidden = true) @Valid ExampleSearchRequestDTO searchRequest) {

        // PageRequestDTO 기능 활용
        Pageable pageable = searchRequest.toPageable("createdAt", Sort.Direction.DESC);
        ExampleSearchDTO searchCondition = searchRequest.getSearchCondition();

        log.debug("[예시 목록 조회 요청] pageNo: {}, pageSize: {}, sortBy: {}, sortDirection: {}, keyword: {}, categoryCode: {}, isActive: {}, startDate: {}, endDate: {}",
                searchRequest.getPageNumber(),
                searchRequest.getPageSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection(),
                searchCondition.getKeyword(),
                searchCondition.getCategoryCode(),
                searchCondition.getIsActive(),
                searchCondition.getStartDate(),
                searchCondition.getEndDate());

        // 페이지네이션을 위한 예시 데이터 생성
        List<ExampleResponseDTO> examples = new ArrayList<>();
        long totalElements = 50; // 가정: 총 50개의 항목이 있다고 가정

        // 현재 페이지에 해당하는 데이터만 반환
        int startIndex = searchRequest.getOffset();
        int endIndex = Math.min(startIndex + searchRequest.getPageSize(), (int) totalElements);

        for (int i = startIndex; i < endIndex; i++) {
            ExampleResponseDTO item = ExampleResponseDTO.builder()
                    .id((long) (i + 1))
                    .title("예시 제목 " + (i + 1))
                    .content("예시 내용입니다")
                    .categoryCode(i % 2 == 0 ? "CATEGORY_A" : "CATEGORY_B")
                    .isActive(true)
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .updatedAt(LocalDateTime.now().minusDays(i).plusHours(2))
                    .build();

            examples.add(item);
        }

        // 페이지 응답 객체 생성
        PageResponseDTO<ExampleResponseDTO> pageResponse = PageResponseDTO.<ExampleResponseDTO>builder()
                .content(examples)
                .pageNumber(searchRequest.getPageNumber())
                .pageSize(searchRequest.getPageSize())
                .totalElements(totalElements)
                .build();

        return ResponseEntity.ok(CustomApiResponse.success(pageResponse));
    }
}