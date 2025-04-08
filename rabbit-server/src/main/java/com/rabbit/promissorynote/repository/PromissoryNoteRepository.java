package com.rabbit.promissorynote.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rabbit.promissorynote.domain.entity.PromissoryNoteEntity;

/**
 * 차용증 NFT 저장소
 */
@Repository
public interface PromissoryNoteRepository extends JpaRepository<PromissoryNoteEntity, Long> {

    /**
     * 삭제되지 않은 차용증 조회
     *
     * @param tokenId 토큰 ID
     * @return 차용증 NFT 정보
     */
    Optional<PromissoryNoteEntity> findByTokenIdAndDeletedFlagFalse(BigInteger tokenId);

    /**
     * 채권자 지갑 주소로 소유한 차용증 목록 조회
     *
     * @param walletAddress 채권자 지갑 주소
     * @return 차용증 NFT 목록
     */
    List<PromissoryNoteEntity> findByCreditorWalletAddressAndDeletedFlagFalse(String walletAddress);

    /**
     * 채무자 지갑 주소로 빌린 차용증 목록 조회
     *
     * @param walletAddress 채무자 지갑 주소
     * @return 차용증 NFT 목록
     */
    List<PromissoryNoteEntity> findByDebtorWalletAddressAndDeletedFlagFalse(String walletAddress);

    /**
     * 만기일 오름차순 정렬된 활성 차용증 목록 조회
     *
     * @return 차용증 NFT 목록
     */
    @Query("SELECT p FROM PromissoryNoteEntity p WHERE p.deletedFlag = false ORDER BY p.maturityDate ASC")
    List<PromissoryNoteEntity> findAllActiveOrderByMaturityDateAsc();

    /**
     * 토큰 ID 목록으로 삭제되지 않은 차용증 목록 조회
     *
     * @param tokenIds 토큰 ID 목록
     * @return 차용증 NFT 목록
     */
    @Query("SELECT p FROM PromissoryNoteEntity p WHERE p.tokenId IN :tokenIds AND p.deletedFlag = false")
    List<PromissoryNoteEntity> findByTokenIdsAndDeletedFlagFalse(@Param("tokenIds") List<BigInteger> tokenIds);

    /**
     * 토큰 ID로 addTermsHash 값 조회
     *
     * @param tokenId 토큰 ID
     * @return addTermsHash 값 (PDF URI)
     */
    @Query("SELECT p.addTermsHash FROM PromissoryNoteEntity p WHERE p.tokenId = :tokenId AND p.deletedFlag = false")
    Optional<String> findAddTermsHashByTokenId(@Param("tokenId") BigInteger tokenId);
}