package com.rabbit.global.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageResponseDTO<T> {
    // 컨텐츠
    private List<T> content;
    
    // 페이지 정보
    private int pageNo;          // 현재 페이지 번호
    private int pageSize;        // 페이지 크기
    private long totalElements;  // 총 요소 수
    private int totalPages;      // 총 페이지 수
    
    // 페이지 상태
    private boolean first;       // 첫 페이지 여부
    private boolean last;        // 마지막 페이지 여부
    private boolean empty;       // 결과가 비어있는지 여부
    
    @Builder
    public PageResponseDTO(List<T> content, int pageNo, int pageSize, long totalElements) {
        this.content = content;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        
        // 총 페이지 수 계산
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        // 페이지 상태 계산
        this.first = pageNo == 0;
        this.last = pageNo == totalPages - 1;
        this.empty = content.isEmpty();
    }
}
/*
	// User 타입으로 사용할 경우
	PageResponseDTO<User> userPage = new PageResponseDTO<>(
	    userList,      // List<User> 타입
	    0,             // pageNo
	    10,            // pageSize
	    50,            // totalElements
	    false          // last
	);
*/
