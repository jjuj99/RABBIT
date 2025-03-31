package com.rabbit.global.code.repository;

import com.rabbit.global.code.domain.entity.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    /**
     * 코드 타입과 코드로 단일 코드 조회
     */
    Optional<CommonCode> findByCodeTypeAndCode(String codeType, String code);

    /**
     * 코드 타입에 해당하는 모든 코드를 표시 순서대로 조회
     */
    List<CommonCode> findByCodeTypeOrderByDisplayOrder(String codeType);

    /**
     * 코드 타입과 활성화 상태에 해당하는 코드를 표시 순서대로 조회
     */
    List<CommonCode> findByCodeTypeAndActiveFlagOrderByDisplayOrder(String codeType, boolean active);

    /**
     * 코드 타입과 코드가 이미 존재하는지 확인
     */
    boolean existsByCodeTypeAndCode(String codeType, String code);

    /**
     * 특정 코드 타입에 해당하는 코드 수 카운트
     */
    long countByCodeType(String codeType);

    /**
     * 이미 존재하는 코드 타입과 코드 쌍 조회
     */
    @Query("SELECT c.codeType as codeType, c.code as code FROM CommonCode c WHERE c.codeType IN :codeTypes AND c.code IN :codes")
    List<CodeTypePair> findExistingCodeTypeAndCodePairs(
            @Param("codeTypes") Set<String> codeTypes,
            @Param("codes") Set<String> codes);

    /**
     * 활성화된 코드만 조회
     */
    List<CommonCode> findByActiveFlagTrue();

    /**
     * 특정 코드 타입과 코드 이름으로 코드 검색
     */
    List<CommonCode> findByCodeTypeAndCodeNameContaining(String codeType, String keyword);

    /**
     * 특정 코드 타입에 대해 표시 순서 최대값 조회
     */
    @Query("SELECT MAX(c.displayOrder) FROM CommonCode c WHERE c.codeType = :codeType")
    Integer findMaxDisplayOrderByCodeType(@Param("codeType") String codeType);

    /**
     * 여러 코드 타입에 해당하는 코드 목록 조회
     */
    List<CommonCode> findByCodeTypeIn(Set<String> codeTypes);

    /**
     * 모든 고유한 코드 타입 조회
     */
    @Query("SELECT DISTINCT c.codeType FROM CommonCode c")
    Set<String> findAllCodeTypes();

    /**
     * 코드 타입별 코드 수 조회
     */
    @Query("SELECT c.codeType as codeType, COUNT(c) as count FROM CommonCode c GROUP BY c.codeType")
    List<CodeTypeCount> countGroupByCodeType();

    /**
     * 프로젝션 인터페이스 - 코드 타입과 코드 쌍
     */
    interface CodeTypePair {
        String getCodeType();
        String getCode();
    }

    /**
     * 프로젝션 인터페이스 - 코드 타입별 카운트
     */
    interface CodeTypeCount {
        String getCodeType();
        Long getCount();
    }
}