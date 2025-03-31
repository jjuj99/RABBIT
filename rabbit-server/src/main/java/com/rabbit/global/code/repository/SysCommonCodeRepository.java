package com.rabbit.global.code.repository;

import com.rabbit.global.code.domain.entity.SysCommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SysCommonCodeRepository extends JpaRepository<SysCommonCode, Long> {

    /**
     * 코드 타입과 코드로 단일 코드 조회
     */
    Optional<SysCommonCode> findByCodeTypeAndCode(String codeType, String code);

    /**
     * 코드 타입에 해당하는 모든 코드를 표시 순서대로 조회
     */
    List<SysCommonCode> findByCodeTypeOrderByDisplayOrder(String codeType);

    /**
     * 코드 타입과 활성화 상태에 해당하는 코드를 표시 순서대로 조회
     */
    List<SysCommonCode> findByCodeTypeAndActiveFlagOrderByDisplayOrder(String codeType, boolean active);

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
    @Query("SELECT s.codeType as codeType, s.code as code FROM SysCommonCode s WHERE s.codeType IN :codeTypes AND s.code IN :codes")
    List<CodeTypePair> findExistingCodeTypeAndCodePairs(
            @Param("codeTypes") Set<String> codeTypes,
            @Param("codes") Set<String> codes);

    /**
     * 모든 고유한 코드 타입 조회
     */
    @Query("SELECT DISTINCT s.codeType FROM SysCommonCode s")
    Set<String> findAllCodeTypes();

    /**

     * 프로젝션 인터페이스 - 코드 타입과 코드 쌍

     */

    interface CodeTypePair {
        String getCodeType();
        String getCode();
    }
}