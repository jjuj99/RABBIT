package com.rabbit.contract.domain.dto.request;

import com.rabbit.global.request.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "계약 목록 조회를 위한 검색 요청")
@Getter
@Setter
public class ContractSearchRequestDTO extends PageRequestDTO {

    @Schema(description = "검색 조건")
    private ContractSearchDTO searchCondition = new ContractSearchDTO();

    // 기본 생성자
    public ContractSearchRequestDTO() {
        super();
    }

    // 페이지 정보만 설정하는 생성자
    public ContractSearchRequestDTO(int pageNumber, int pageSize) {
        super(pageNumber, pageSize);
    }

    // 페이지 정보와 정렬 정보를 설정하는 생성자
    public ContractSearchRequestDTO(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        super(pageNumber, pageSize, sortBy, sortDirection);
    }
}