package com.rabbit.example.domain.dto.request;

import com.rabbit.global.request.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Schema(description = "예시 목록 조회를 위한 검색 요청")
@Getter
@Setter
public class ExampleSearchRequestDTO extends PageRequestDTO {

    @Schema(description = "검색 조건")
    private ExampleSearchDTO searchCondition = new ExampleSearchDTO();

    // 기본 생성자
    public ExampleSearchRequestDTO() {
        super();
    }

    // 페이지 정보만 설정하는 생성자
    public ExampleSearchRequestDTO(int pageNo, int pageSize) {
        super(pageNo, pageSize);
    }

    // 페이지 정보와 정렬 정보를 설정하는 생성자
    public ExampleSearchRequestDTO(int pageNo, int pageSize, String sortBy, String sortDirection) {
        super(pageNo, pageSize, sortBy, sortDirection);
    }

}