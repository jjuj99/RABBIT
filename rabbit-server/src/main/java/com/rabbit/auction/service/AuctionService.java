package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.*;
import com.rabbit.auction.domain.entity.Bid;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.bankApi.service.BankService;
import com.rabbit.blockchain.domain.dto.RepaymentInfo;
import com.rabbit.blockchain.domain.dto.response.AppendixMetadataDTO;
import com.rabbit.blockchain.mapper.AppendixMetadataMapper;
import com.rabbit.blockchain.service.EventService;
import com.rabbit.blockchain.service.PromissoryNoteAuctionService;
import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.contract.service.ContractService;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.ipfs.PinataUploader;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.global.util.DateTimeUtils;
import com.rabbit.global.util.IntegrityHashUtil;
import com.rabbit.global.util.LoanUtil;
import com.rabbit.loan.domain.dto.response.ContractEventDTO;
import com.rabbit.mail.service.ExtendedMailService;
import com.rabbit.mail.service.MailService;
import com.rabbit.notification.domain.dto.request.NotificationRequestDTO;
import com.rabbit.notification.service.NotificationService;
import com.rabbit.promissorynote.domain.entity.PromissoryNoteEntity;
import com.rabbit.promissorynote.repository.PromissoryNoteRepository;
import com.rabbit.sse.service.SseEventPublisher;
import com.rabbit.user.domain.dto.response.ProfileInfoResponseDTO;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.MetamaskWalletRepository;
import com.rabbit.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
// 다른 import 문은 동일하게 유지

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionScheduler auctionScheduler;
    private final SseEventPublisher sseEventPublisher;
    private final NotificationService notificationService;
    private final PromissoryNoteAuctionService promissoryNoteAuctionService;
    private final UserService userService;
    private final ContractRepository contractRepository;
    private final MailService mailService;
    private final ContractService contractService;
    private final PromissoryNoteService promissoryNoteService;
    private final BankService bankService;
    private final LoanUtil loanUtil;
    private final EventService eventService;
    private final PinataUploader pinataUploader;
    private final AuctionTransferPdfService auctionTransferPdfService;
    private final IntegrityHashUtil integrityHashUtil;
    private final AuctionTransferNoticePdfService auctionTransferNoticePdfService;
    private final ExtendedMailService extendedMailService;
    private final PromissoryNoteRepository promissoryNoteRepository;
    private final MetamaskWalletRepository metamaskWalletRepository;

    private final SysCommonCodeService sysCommonCodeService;
    private final RepaymentSchedulerService repaymentSchedulerService;

    // 코드 타입 상수 정의
    private static final String AUCTION_STATUS = SysCommonCodes.Auction.values()[0].getCodeType();
    private static final String BID_STATUS = SysCommonCodes.Bid.values()[0].getCodeType();



    public AuctionIdDTO addAuction(@Valid AuctionRequestDTO auctionRequest, Integer userId) {
        log.info("[경매생성시작] 사용자ID={}, 요청토큰ID={}, 최소입찰가={}, 종료일={}",
                userId, auctionRequest.getTokenId(), auctionRequest.getMinimumBid(), auctionRequest.getEndDate());

        String tokenId = auctionRequest.getTokenId().toString();
        PromissoryNoteEntity promissoryNote = promissoryNoteRepository.findById(Long.parseLong(tokenId))
                .orElseThrow(() -> {
                    log.error("[경매생성실패] 토큰ID={}에 해당하는 계약을 찾을 수 없음", tokenId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 tokenId의 계약이 없습니다.");
                });

        String account = metamaskWalletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElseThrow(() -> {
                    log.error("[경매생성실패] 사용자ID={}의 주 지갑을 찾을 수 없음", userId);
                    return new BusinessException(ErrorCode.WALLET_NOT_FOUND, "사용자의 주 지갑을 찾을 수 없습니다");
                });

        log.debug("[경매생성검증] 계약자지갑주소={}, 사용자지갑주소={}", promissoryNote.getCreditorWalletAddress(), account);

        if(!promissoryNote.getCreditorWalletAddress().equals(account)){
            log.error("[경매생성실패] 사용자ID={}가 NFT 소유자가 아님", userId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "해당 NFT 경매의 권한이 없습니다.");
        }

        //이미 경매가 진행중인지 확인
        auctionRepository.findByTokenIdAndAuctionStatus(auctionRequest.getTokenId(), SysCommonCodes.Auction.ING)
                .ifPresent(auction ->{
                    log.error("[경매생성실패] 토큰ID={}는 이미 경매가 진행 중 (경매ID={})",
                            auctionRequest.getTokenId(), auction.getAuctionId());
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "해당 NFT는 이미 경매가 진행 중입니다.");
                });

        User assignor = userService.findById(userId);
        log.debug("[경매생성정보] 양도인정보 ID={}, 이름={}", assignor.getUserId(), assignor.getUserName());

        Auction auction = Auction.builder()
                .assignor(assignor)
                .minimumBid(auctionRequest.getMinimumBid())
                .endDate(auctionRequest.getEndDate())
                .tokenId(auctionRequest.getTokenId())
                .auctionStatus(SysCommonCodes.Auction.ING)
                .sellerSign(null)
                .createdAt(ZonedDateTime.now())
                .build();

        Auction savedAuction = auctionRepository.save(auction);
        log.info("[경매생성완료] 경매ID={}, 토큰ID={}, 양도인ID={}, 최소입찰가={}, 종료일={}",
                savedAuction.getAuctionId(), savedAuction.getTokenId(), userId,
                savedAuction.getMinimumBid(), savedAuction.getEndDate());

        auctionScheduler.scheduleAuctionEnd(savedAuction.getAuctionId(), savedAuction.getEndDate());
        log.debug("[경매종료스케줄링] 경매ID={}, 종료일={}", savedAuction.getAuctionId(), savedAuction.getEndDate());

        return AuctionIdDTO.builder()
                .auctionId(savedAuction.getAuctionId())
                .build();
    }

    public PageResponseDTO<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO request, Pageable pageable) {
        log.info("[경매검색시작] 가격조건: 최소={}, 최대={}, 이자율조건: 최소={}, 최대={}, 상환방식={}, 만기기간={}",
                request.getMinPrice(), request.getMaxPrice(),
                request.getMinIr(), request.getMaxIr(),
                request.getRepayType(), request.getMatTerm());

        Page<AuctionResponseDTO> result = auctionRepository.searchAuctions(request, pageable);
        log.debug("[경매검색DB결과] 총 {}건 조회됨", result.getTotalElements());

        //블록체인 읽어와 다른 조건 필터링 구현 필요
        List<AuctionResponseDTO> fullList = result.getContent().stream()
                .map(dto -> {
                    try {
                        log.debug("[경매상세조회] 경매ID={}, 토큰ID={} 정보 블록체인 조회 시작",
                                dto.getAuctionId(), dto.getTokenId());

                        // 블록체인 메타데이터 조회
                        PromissoryNote.PromissoryMetadata metadata = promissoryNoteService.getPromissoryMetadata(dto.getTokenId());
                        log.trace("[블록체인메타데이터] 토큰ID={}, 만기일={}, 이자율={}, 상환방식={}",
                                dto.getTokenId(), metadata.matDt, metadata.ir, metadata.repayType);

                        // 상환 정보 조회 (연체 횟수, 남은 원금)
                        RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(dto.getTokenId());
                        log.trace("[상환정보] 토큰ID={}, 남은원금={}, 남은납입횟수={}, 연체횟수={}",
                                dto.getTokenId(), repaymentInfo.remainingPrincipal,
                                repaymentInfo.remainingPayments, repaymentInfo.overdueInfo.defCnt);

                        // 채무자 조회 및 신용 점수 조회
                        User debtor = contractService.getDebtorByTokenId(dto.getTokenId());
                        String creditScore = bankService.getCreditScore(debtor.getUserId());
                        log.trace("[채무자정보] 토큰ID={}, 채무자ID={}, 채무자명={}, 신용점수={}",
                                dto.getTokenId(), debtor.getUserId(), debtor.getUserName(), creditScore);

                        ZonedDateTime matDt = DateTimeUtils.toZonedDateTimeAtEndOfDay(metadata.matDt);

                        // 만기 수취액 계산
                        BigDecimal ir = new BigDecimal(metadata.ir).divide(BigDecimal.valueOf(10000));

                        // 만기수취액 계산
                        BigDecimal totalAmount = loanUtil.calculateTotalRepaymentAmount(
                                new BigDecimal(repaymentInfo.remainingPrincipal),
                                ir,
                                repaymentInfo.remainingPayments.intValue(),
                                SysCommonCodes.Repayment.toCalculationType(metadata.repayType),
                                LoanUtil.RoundingStrategy.HALF_UP,
                                LoanUtil.TruncationStrategy.WON,
                                LoanUtil.LegalLimits.getDefaultLimits()
                        );
                        log.debug("[만기수취액계산] 토큰ID={}, 남은원금={}, 이자율={}, 만기수취액={}",
                                dto.getTokenId(), repaymentInfo.remainingPrincipal, ir, totalAmount);

                        return AuctionResponseDTO.builder()
                                .auctionId(dto.getAuctionId())
                                .price(dto.getPrice())
                                .endDate(dto.getEndDate())
                                .createdAt(dto.getCreatedAt())
                                .ir(ir)
                                .tokenId(dto.getTokenId())
                                .repayType(SysCommonCodes.Repayment.fromCode(metadata.repayType).getCodeName())
                                .totalAmount(totalAmount.longValue())
                                .matDt(matDt)
                                .dir(new BigDecimal(metadata.dir))
                                .la(repaymentInfo.remainingPrincipal.longValue())
                                .earlypayFlag(metadata.earlyPayFlag)
                                .earlypayFee(new BigDecimal(metadata.earlyPayFee))
                                .creditScore(creditScore)
                                .defCnt(repaymentInfo.overdueInfo.defCnt.intValue())
                                .nftImageUrl(metadata.nftImage)
                                .build();

                    } catch (Exception e) {
                        log.warn("[경매목록변환오류] 경매ID={} 처리 중 예외 발생: {}",
                                dto.getAuctionId(), e.getMessage(), e);
                        return null; // 예외 시 필터링 제외
                    }
                })
                .filter(Objects::nonNull)
                .filter(dto -> {
                    // 블록체인 기반 필터 조건 적용
                    boolean irCheck = (request.getMinIr() == null || dto.getIr().compareTo(request.getMinIr()) >= 0)
                            && (request.getMaxIr() == null || dto.getIr().compareTo(request.getMaxIr()) <= 0);

                    // 상환 방식 여러 개 선택 가능 (IN 조건)
                    boolean repayCheck = true;
                    if (request.getRepayType() != null && !request.getRepayType().isEmpty()) {
                        SysCommonCodes.Repayment repaymentEnum = SysCommonCodes.Repayment.fromCodeEnumName(dto.getRepayType()); // 한글 → Enum
                        repayCheck = request.getRepayType().contains(repaymentEnum.getDisplayOrder());
                        log.trace("[상환방식필터] 경매ID={}, 상환방식={}, 필터결과={}",
                                dto.getAuctionId(), repaymentEnum.getDisplayOrder(), repayCheck);
                    }

                    // 만기일 필터링
                    boolean matCheck = true;
                    if (request.getMatTerm() != null) {
                        ZonedDateTime now = ZonedDateTime.now();
                        switch (request.getMatTerm()) {
                            case 1 -> matCheck = dto.getMatDt().isBefore(now.plusMonths(1));
                            case 3 -> matCheck = dto.getMatDt().isBefore(now.plusMonths(3));
                            case 6 -> matCheck = dto.getMatDt().isBefore(now.plusMonths(6));
                            case 12 -> matCheck = dto.getMatDt().isBefore(now.plusMonths(12));
                        }
                        log.trace("[만기일필터] 경매ID={}, 만기일={}, 필터기간={}개월, 필터결과={}",
                                dto.getAuctionId(), dto.getMatDt(), request.getMatTerm(), matCheck);
                    } else if (request.getMatStart() != null && request.getMatEnd() != null) {
                        matCheck = !dto.getMatDt().isBefore(request.getMatStart()) && !dto.getMatDt().isAfter(request.getMatEnd());
                        log.trace("[만기일필터] 경매ID={}, 만기일={}, 필터범위={}~{}, 필터결과={}",
                                dto.getAuctionId(), dto.getMatDt(), request.getMatStart(), request.getMatEnd(), matCheck);
                    }

                    boolean finalResult = irCheck && repayCheck && matCheck;
                    if (!finalResult) {
                        log.trace("[필터제외] 경매ID={}, 이유: 이자율={}, 상환방식={}, 만기일={}",
                                dto.getAuctionId(), irCheck, repayCheck, matCheck);
                    }
                    return finalResult;
                })
                .toList();

        // 블록체인 필터링 후 다시한번 페이징
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        log.debug("[최종필터링결과] 총 {}건 중 {}건 표시 (페이지={}, 크기={})",
                fullList.size(), Math.min(limit, fullList.size() - offset),
                pageable.getPageNumber(), pageable.getPageSize());

        List<AuctionResponseDTO> pagedList = fullList.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        return PageResponseDTO.<AuctionResponseDTO>builder()
                .content(pagedList)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(fullList.size()) // 전체 개수는 필터링된 데이터 기준
                .build();
    }

    public void cancelAuction(@Valid Integer auctionId, Integer userId) {
        log.info("[경매취소시작] 경매ID={}, 요청자ID={}", auctionId, userId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    log.error("[경매취소실패] 경매ID={}를 찾을 수 없음", auctionId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
                });
        log.debug("[경매취소검증] 경매ID={}, 토큰ID={}, 양도인ID={}, 요청자ID={}, 경매상태={}",
                auction.getAuctionId(), auction.getTokenId(), auction.getAssignor().getUserId(),
                userId, auction.getAuctionStatus());

        // 내 auction이 아니면 취소 불가
        if(!auction.getAssignor().getUserId().equals(userId)){
            log.error("[경매취소실패] 경매ID={}, 요청자ID={}는 양도인ID={}와 다름",
                    auctionId, userId, auction.getAssignor().getUserId());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "경매 취소 권한이 없습니다.");
        }

        //입찰자 존재시 취소 불가
        boolean hasBids=bidRepository.existsByAuction(auction);
        log.debug("[경매취소검증] 경매ID={}, 입찰존재여부={}", auctionId, hasBids);
        if(hasBids){
            log.error("[경매취소실패] 경매ID={}에 입찰내역이 존재하여 취소 불가", auctionId);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰자가 존재해 경매를 취소할 수 없습니다.");
        }

        //블록체인에서 경매 취소
        try {
            log.debug("[블록체인경매취소] 토큰ID={} 경매취소 시작", auction.getTokenId());
            promissoryNoteAuctionService.cancelAuction(auction.getTokenId());
            log.info("[블록체인경매취소성공] 토큰ID={}", auction.getTokenId());
        } catch (Exception e) {
            log.error("[블록체인경매취소실패] 토큰ID={}, 오류={}", auction.getTokenId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "경매 취소 중 오류가 발생했습니다.");
        }

        //cancel로 상태 변경
        auction.setAuctionStatus(SysCommonCodes.Auction.CANCELED);
        log.debug("[경매상태변경] 경매ID={}, 상태변경: {}→{}",
                auction.getAuctionId(), SysCommonCodes.Auction.ING, SysCommonCodes.Auction.CANCELED);

        auctionRepository.save(auction);
        log.info("[경매취소완료] 경매ID={}, 토큰ID={}, 요청자ID={}",
                auction.getAuctionId(), auction.getTokenId(), userId);
    }

    // 다른 메소드들도 비슷한 방식으로 로깅 추가...

    public void processAuctionEnd(Integer auctionId) {
        log.info("[경매종료처리시작] 경매ID={}", auctionId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    log.error("[경매종료실패] 경매ID={}를 찾을 수 없음", auctionId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
                });
        log.debug("[경매종료상태확인] 경매ID={}, 현재상태={}", auctionId, auction.getAuctionStatus());

        if (auction.getAuctionStatus() != SysCommonCodes.Auction.ING) {
            log.error("[경매종료실패] 경매ID={}는 진행중 상태가 아님 (현재상태={})",
                    auctionId, auction.getAuctionStatus());
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "진행중인 경매가 아닙니다.");
        }

        List<Bid> bids = bidRepository.findAllByAuction_AuctionIdOrderByBidAmountDescCreatedAtAsc(auctionId);
        log.debug("[경매입찰내역조회] 경매ID={}, 입찰건수={}", auctionId, bids.size());

        if (bids.isEmpty()) {   //낙찰자가 없는 경우
            log.info("[경매실패처리] 경매ID={}, 입찰내역없음", auctionId);
            auction.setAuctionStatus(SysCommonCodes.Auction.FAILED);

            //양도자에게 알림
            notificationService.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(auction.getAssignor().getUserId())
                            .type(SysCommonCodes.NotificationType.AUCTION_FAILED)
                            .relatedId(auctionId)
                            .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                            .build()
            );
            log.debug("[경매실패알림] 양도인ID={}에게 경매실패 알림 발송", auction.getAssignor().getUserId());
        } else {
            Bid winningBid = bids.get(0);   //최고가
            log.info("[낙찰처리시작] 경매ID={}, 낙찰자ID={}, 낙찰가격={}",
                    auctionId, winningBid.getUserId(), winningBid.getBidAmount());

            auction.updatePriceAndBidder(winningBid.getBidAmount(), winningBid.getUserId());
            auction.setAuctionStatus(SysCommonCodes.Auction.COMPLETED);
            log.debug("[경매상태변경] 경매ID={}, 상태변경: {}→{}",
                    auction.getAuctionId(), SysCommonCodes.Auction.ING, SysCommonCodes.Auction.COMPLETED);

            // 낙찰 종료 트랜잭션 실행
            ProfileInfoResponseDTO grantor = userService.getProfileInfo(auction.getAssignor().getUserId());
            ProfileInfoResponseDTO grantee = userService.getProfileInfo(winningBid.getUserId());
            log.debug("[양도양수자정보] 양도인ID={}, 양도인명={}, 양수인ID={}, 양수인명={}",
                    grantor.getUserId(), grantor.getUserName(), grantee.getUserId(), grantee.getUserName());

            // 상환 정보 조회 (연체 횟수, 남은 원금)
            try {
                log.debug("[상환정보조회] 토큰ID={} 상환정보 블록체인 조회 시작", auction.getTokenId());
                RepaymentInfo repaymentInfo = repaymentSchedulerService.getRepaymentInfo(auction.getTokenId());
                log.debug("[상환정보조회결과] 토큰ID={}, 남은원금={}, 연체횟수={}",
                        auction.getTokenId(), repaymentInfo.remainingPrincipal, repaymentInfo.defCnt);

                // 채무자 정보 가져오기
                User debtor = contractService.getDebtorByTokenId(auction.getTokenId());
                MetamaskWallet debtorWallet = userService.getWalletByUserIdAndPrimaryFlagTrue(debtor.getUserId());
                log.debug("[채무자정보] 채무자ID={}, 채무자명={}, 지갑주소={}",
                        debtor.getUserId(), debtor.getUserName(), debtorWallet.getWalletAddress());

                // pdf 만들기
                log.debug("[양도계약서생성시작] 경매ID={}, 토큰ID={}", auctionId, auction.getTokenId());
                byte[] transferPdfBytes = auctionTransferPdfService.generateTransferAgreementPdf(
                        grantor.getUserName(),
                        grantor.getWalletAddress(),
                        grantee.getUserName(),
                        grantee.getWalletAddress(),
                        debtor.getUserName(),
                        debtorWallet.getWalletAddress(),
                        new BigDecimal(repaymentInfo.remainingPrincipal),
                        LocalDate.now()
                );
                log.debug("[양도계약서생성완료] 경매ID={}, PDF크기={}bytes", auctionId, transferPdfBytes.length);

                // pdf pinata에 업로드하기
                String pdfFileName = "양도양수계약서_" + auction.getAuctionId() + ".pdf";
                log.debug("[IPFS업로드시작] 파일명={}", pdfFileName);
                String pdfUrl = pinataUploader.uploadContent(transferPdfBytes, pdfFileName, "application/pdf");
                log.info("[IPFS업로드완료] 파일명={}, URL={}", pdfFileName, pdfUrl);

                auction.setContractIpfsUrl(pdfUrl);

                // 해시값 구하기
                log.debug("[무결성해시생성] 양도인={}, 양도인이메일={}, 양도인지갑={}",
                        grantor.getUserName(), grantor.getEmail(), grantor.getWalletAddress());
                String grantorHash = IntegrityHashUtil.generateIntegrityHash(
                        grantor.getUserName(), grantor.getEmail(), grantor.getWalletAddress());

                log.debug("[무결성해시생성] 양수인={}, 양수인이메일={}, 양수인지갑={}",
                        grantee.getUserName(), grantee.getEmail(), grantee.getWalletAddress());
                String granteeHash = IntegrityHashUtil.generateIntegrityHash(
                        grantee.getUserName(), grantee.getEmail(), grantee.getWalletAddress());

                AppendixMetadataDTO dto = AppendixMetadataDTO.builder()
                        .tokenId(auction.getTokenId())
                        .grantorSign(auction.getSellerSign())
                        .grantorName(grantor.getUserName())
                        .grantorWalletAddress(grantor.getWalletAddress())
                        .grantorInfoHash(grantorHash)
                        .granteeSign(winningBid.getBidderSign())
                        .granteeName(grantee.getUserName())
                        .granteeWalletAddress(grantee.getWalletAddress())
                        .granteeInfoHash(granteeHash)
                        .la(repaymentInfo.remainingPrincipal)
                        .contractDate(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .originalText(pdfUrl)
                        .build();
                log.debug("[부속NFT메타데이터준비] 토큰ID={}, 계약일={}, 원금={}",
                        dto.getTokenId(), dto.getContractDate(), dto.getLa());

                // 부속 nft 발행
                PromissoryNoteAuction.AppendixMetadata metadata = AppendixMetadataMapper.toWeb3(dto);

                MetamaskWallet currentBidderWallet = userService.getWalletByUserIdAndPrimaryFlagTrue(winningBid.getUserId());
                log.debug("[낙찰자지갑정보] 낙찰자ID={}, 지갑주소={}",
                        winningBid.getUserId(), currentBidderWallet.getWalletAddress());

                log.info("[블록체인경매완료처리시작] 토큰ID={}, 낙찰가격={}, 낙찰자지갑={}",
                        auction.getTokenId(), winningBid.getBidAmount(), currentBidderWallet.getWalletAddress());
                promissoryNoteAuctionService.finalizeAuction(
                        auction.getTokenId(),
                        currentBidderWallet.getWalletAddress(),
                        BigInteger.valueOf(winningBid.getBidAmount()),
                        metadata
                );
                log.info("[블록체인경매완료처리성공] 토큰ID={}", auction.getTokenId());

                // 낙찰자에게 성공 알림
                notificationService.createNotification(
                        NotificationRequestDTO.builder()
                                .userId(winningBid.getUserId())
                                .type(SysCommonCodes.NotificationType.AUCTION_SUCCESS)
                                .relatedId(auctionId)
                                .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                                .build()
                );
                log.debug("[낙찰성공알림] 양수인ID={}에게 낙찰성공 알림 발송", winningBid.getUserId());

                User winner = userService.findById(winningBid.getUserId());
                log.debug("[양수인메일발송시작] 수신자={}, 이메일={}, 토큰ID={}",
                        winner.getUserName(), winner.getEmail(), auction.getTokenId());
                extendedMailService.sendAuctionContractToAssignee(
                        winner.getEmail(),
                        winner.getUserName(),
                        auction.getTokenId(),
                        transferPdfBytes
                );
                log.debug("[양수인메일발송완료] 수신자={}", winner.getUserName());

                // 양도자에게 전송 예정 알림
                notificationService.createNotification(
                        NotificationRequestDTO.builder()
                                .userId(auction.getAssignor().getUserId())
                                .type(SysCommonCodes.NotificationType.AUCTION_TRANSFERRED)
                                .relatedId(auctionId)
                                .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                                .build()
                );
                log.debug("[양도완료알림] 양도인ID={}에게 양도완료 알림 발송", auction.getAssignor().getUserId());

                User seller = userService.findById(auction.getAssignor().getUserId());
                log.debug("[양도인메일발송시작] 수신자={}, 이메일={}, 토큰ID={}",
                        seller.getUserName(), seller.getEmail(), auction.getTokenId());
                extendedMailService.sendAuctionContractToAssignor(
                        seller.getEmail(),
                        seller.getUserName(),
                        auction.getTokenId(),
                        transferPdfBytes
                );
                log.debug("[양도인메일발송완료] 수신자={}", seller.getUserName());

                // 채무자에게 알림 메일 전송
                log.debug("[채무자통지서생성시작] 경매ID={}, 채무자={}",
                        auctionId, debtor.getUserName());
                byte[] noticePdfBytes = auctionTransferNoticePdfService.generateTransferNoticePdf(
                        grantor.getUserName(),               // 양도인
                        grantor.getWalletAddress(),
                        grantee.getUserName(),               // 양수인
                        grantee.getWalletAddress(),
                        debtor.getUserName(),                // 제3채무자
                        debtorWallet.getWalletAddress(),
                        new BigDecimal(repaymentInfo.remainingPrincipal), // 채권 금액
                        LocalDate.now()                      // 통지일자
                );
                log.debug("[채무자통지서생성완료] 경매ID={}, PDF크기={}bytes",
                        auctionId, noticePdfBytes.length);

                log.debug("[채무자메일발송시작] 수신자={}, 이메일={}, 경매ID={}",
                        debtor.getUserName(), debtor.getEmail(), auction.getAuctionId());
                extendedMailService.sendAuctionTransferNoticeEmail(
                        debtor.getEmail(),
                        debtor.getUserName(),
                        grantee.getUserName(),
                        noticePdfBytes,
                        transferPdfBytes,
                        auction.getAuctionId()
                );
                log.debug("[채무자메일발송완료] 수신자={}", debtor.getUserName());

            } catch (Exception e) {
                log.error("[경매종료처리실패] 경매ID={}, 블록체인처리오류: {}",
                        auctionId, e.getMessage(), e);
                throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR);
            }
        }

        auctionRepository.save(auction);
        log.info("[경매종료처리완료] 경매ID={}, 최종상태={}, 낙찰여부={}",
                auction.getAuctionId(), auction.getAuctionStatus(),
                auction.getAuctionStatus() == SysCommonCodes.Auction.COMPLETED);

        //차용증 현재 채권자 정보 변경
        log.debug("[채권자정보변경시작] 토큰ID={}, 양도인ID={}",
                auction.getTokenId(), auction.getAssignor().getUserId());
        contractService.changeCreditorByTokenId(auction.getTokenId(), auction.getAssignor().getUserId());
        log.debug("[채권자정보변경완료] 토큰ID={}", auction.getTokenId());
    }

    public PageResponseDTO<MyAuctionResponseDTO> getMyBidAuctions(Integer userId, Pageable pageable) {
        log.info("[내입찰경매조회시작] 사용자ID={}, 페이지={}, 크기={}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<MyAuctionResponseDTO> result = auctionRepository.getMyBidAuction(userId, pageable);
        log.debug("[내입찰경매조회결과] 사용자ID={}, 총 {}건 조회됨", userId, result.getTotalElements());

        // 국제화된 상태명 설정
        result.getContent().forEach(dto -> {
            if (dto.getAuctionStatus() != null) {
                String statusName = sysCommonCodeService.getCodeName(
                        AUCTION_STATUS, dto.getAuctionStatus().getCode());
                dto.setAuctionStatusName(statusName);
                log.trace("[경매상태국제화] 경매ID={}, 상태코드={}, 상태명={}",
                        dto.getAuctionId(), dto.getAuctionStatus().getCode(), statusName);
            }
            if (dto.getBidStatus() != null) {
                String bidStatusName = sysCommonCodeService.getCodeName(
                        BID_STATUS, dto.getBidStatus());
                dto.setBidStatusName(bidStatusName);
                log.trace("[입찰상태국제화] 경매ID={}, 입찰상태코드={}, 상태명={}",
                        dto.getAuctionId(), dto.getBidStatus(), bidStatusName);
            }

            if (dto.getTokenId() != null) {
                Optional<String> imageOpt = promissoryNoteRepository.findNftImageByTokenId(dto.getTokenId());
                imageOpt.ifPresent(image -> {
                    dto.setNftImage(image);
                    log.trace("[NFT이미지설정] 경매ID={}, 토큰ID={}, 이미지URL={}",
                            dto.getAuctionId(), dto.getTokenId(), image);
                });
            }
        });

        log.info("[내입찰경매조회완료] 사용자ID={}, 조회건수={}", userId, result.getContent().size());
        return PageResponseDTO.<MyAuctionResponseDTO>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    public AuctionDetailResponseDTO getAuctionDetail(Integer auctionId) {
        log.info("[경매상세조회시작] 경매ID={}", auctionId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    log.error("[경매상세조회실패] 경매ID={}를 찾을 수 없음", auctionId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
                });
        log.debug("[경매상세기본정보] 경매ID={}, 토큰ID={}, 양도인ID={}, 상태={}",
                auction.getAuctionId(), auction.getTokenId(),
                auction.getAssignor().getUserId(), auction.getAuctionStatus());

        User debtor = contractService.getDebtorByTokenId(auction.getTokenId());
        String creditScore = bankService.getCreditScore(debtor.getUserId());
        log.debug("[채무자정보] 토큰ID={}, 채무자ID={}, 채무자명={}, 신용점수={}",
                auction.getTokenId(), debtor.getUserId(), debtor.getUserName(), creditScore);

        //블록체인에서 직접 읽어온 값 추가 필요
        try {
            log.debug("[블록체인조회시작] 토큰ID={} 메타데이터 조회", auction.getTokenId());
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(auction.getTokenId());
            log.trace("[블록체인메타데이터] 토큰ID={}, 만기일={}, 이자율={}, 상환방식={}",
                    auction.getTokenId(), promissoryMetadata.matDt, promissoryMetadata.ir, promissoryMetadata.repayType);

            log.debug("[상환정보조회시작] 토큰ID={} 상환정보 조회", auction.getTokenId());
            RepaymentInfo repaymentInfo = repaymentSchedulerService.getRepaymentInfo(auction.getTokenId());
            log.trace("[상환정보] 토큰ID={}, 남은원금={}, 남은납입횟수={}, 연체횟수={}",
                    auction.getTokenId(), repaymentInfo.remainingPrincipal,
                    repaymentInfo.remainingPayments, repaymentInfo.defCnt);

            BigDecimal ir = new BigDecimal(promissoryMetadata.ir).divide(BigDecimal.valueOf(10000));
            BigDecimal dir = new BigDecimal(promissoryMetadata.dir).divide(BigDecimal.valueOf(10000));
            BigDecimal earlyPayFee = new BigDecimal(promissoryMetadata.earlyPayFee).divide(BigDecimal.valueOf(10000));
            log.debug("[이율정보변환] 이자율={}%, 연체이율={}%, 조기상환수수료={}%",
                    ir.multiply(BigDecimal.valueOf(100)),
                    dir.multiply(BigDecimal.valueOf(100)),
                    earlyPayFee.multiply(BigDecimal.valueOf(100)));

            // 만기수취액 계산
            log.debug("[만기수취액계산시작] 원금={}, 이율={}, 남은납입횟수={}, 상환방식={}",
                    repaymentInfo.remainingPrincipal, ir, repaymentInfo.remainingPayments,
                    promissoryMetadata.repayType);
            BigDecimal totalAmount = loanUtil.calculateTotalRepaymentAmount(
                    new BigDecimal(repaymentInfo.remainingPrincipal),
                    ir,
                    repaymentInfo.remainingPayments.intValue(),
                    SysCommonCodes.Repayment.toCalculationType(promissoryMetadata.repayType),
                    LoanUtil.RoundingStrategy.HALF_UP,
                    LoanUtil.TruncationStrategy.WON,
                    LoanUtil.LegalLimits.getDefaultLimits()
            );
            log.debug("[만기수취액계산결과] 토큰ID={}, 만기수취액={}",
                    auction.getTokenId(), totalAmount);

            Long curPrice = auction.getPrice()==null? auction.getMinimumBid(): auction.getPrice();
            log.debug("[현재가격정보] 경매ID={}, 현재가격={}, 최소입찰가={}",
                    auctionId, curPrice, auction.getMinimumBid());

            AuctionDetailResponseDTO responseDTO = AuctionDetailResponseDTO.builder()
                    .tokenId(auction.getTokenId())
                    .auctionId(auction.getAuctionId())
                    .price(curPrice)  //현재 가격
                    .ir(ir)
                    .repayType(SysCommonCodes.Repayment.fromCode(promissoryMetadata.repayType).getCodeName())
                    .totalAmount(totalAmount.longValue())
                    .matDt(DateTimeUtils.toZonedDateTimeAtEndOfDay(promissoryMetadata.matDt))
                    .dir(dir)
                    .la(repaymentInfo.remainingPrincipal.longValue())  //원금
                    .earlypayFlag(promissoryMetadata.earlyPayFlag)
                    .earlypayFee(earlyPayFee)
                    .creditScore(creditScore)  //신용 점수
                    .defCnt(repaymentInfo.defCnt.intValue())   //연체 횟수
                    .endDate(auction.getEndDate())
                    .createdAt(auction.getCreatedAt())
                    .nftImageUrl(promissoryMetadata.nftImage)
                    .build();

            log.info("[경매상세조회완료] 경매ID={}, 토큰ID={}, 가격={}, 만기일={}",
                    responseDTO.getAuctionId(), responseDTO.getTokenId(),
                    responseDTO.getPrice(), responseDTO.getMatDt());

            return responseDTO;

        } catch (Exception e) {
            log.error("[경매상세조회실패] 경매ID={}, 블록체인오류: {}",
                    auctionId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR);
        }
    }

    public SimilarAuctionResponseDTO getSimilarAuctions(@Valid Integer auctionId) {
        log.info("[유사경매검색시작] 경매ID={}", auctionId);

        Auction targetAuction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    log.error("[유사경매검색실패] 경매ID={}를 찾을 수 없음", auctionId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
                });
        log.debug("[유사경매기준정보] 경매ID={}, 토큰ID={}", auctionId, targetAuction.getTokenId());

        // 2. 기준 값 추출
        // 남은 원금, 남은 상환일 조회 => nft에서 조회
        try {
            log.debug("[블록체인메타데이터조회] 토큰ID={} 메타데이터 조회", targetAuction.getTokenId());
            PromissoryNote.PromissoryMetadata metadata = promissoryNoteService.getPromissoryMetadata(BigInteger.valueOf(targetAuction.getAuctionId()));

            log.debug("[상환정보조회] 토큰ID={} 상환정보 조회", targetAuction.getTokenId());
            RepaymentInfo repaymentInfo = repaymentSchedulerService.getRepaymentInfo(targetAuction.getTokenId());

            Long basePrincipal = repaymentInfo.remainingPrincipal.longValue();
            long baseDays = ChronoUnit.DAYS.between(LocalDate.now(),  LocalDate.parse(metadata.matDt));
            baseDays = Math.max(baseDays, 0);
            Integer baseDaysInt = (int) baseDays;
            log.debug("[유사경매기준값] 남은원금={}, 남은일수={}", basePrincipal, baseDaysInt);

            // 현재 수익률 (기대가격-현재가격)/100?
            BigDecimal ir = new BigDecimal(metadata.ir).divide(BigDecimal.valueOf(10000));

            // 만기수취액 계산
            log.debug("[만기수취액계산시작] 원금={}, 이율={}, 남은납입횟수={}, 상환방식={}",
                    repaymentInfo.remainingPrincipal, ir, repaymentInfo.remainingPayments,
                    metadata.repayType);
            BigDecimal totalAmount = loanUtil.calculateTotalRepaymentAmount(
                    new BigDecimal(repaymentInfo.remainingPrincipal),
                    ir,
                    repaymentInfo.remainingPayments.intValue(),
                    SysCommonCodes.Repayment.toCalculationType(metadata.repayType),
                    LoanUtil.RoundingStrategy.HALF_UP,
                    LoanUtil.TruncationStrategy.WON,
                    LoanUtil.LegalLimits.getDefaultLimits()
            );
            log.debug("[만기수취액계산결과] 총수취액={}", totalAmount);

            // 현재 가격
            BigDecimal currentPrice = BigDecimal.valueOf(
                    targetAuction.getPrice() != null ? targetAuction.getPrice() : targetAuction.getMinimumBid()
            );
            log.debug("[현재가격] 현재가격={}", currentPrice);

            // (totalAmount - currentPrice) / currentPrice * 100
            BigDecimal diff = totalAmount.subtract(currentPrice);
            BigDecimal rate = diff.divide(currentPrice, 6, RoundingMode.HALF_UP); // 소수점 6자리까지
            BigDecimal currentRR = rate.multiply(BigDecimal.valueOf(100)); // 퍼센트로 변환
            log.debug("[수익률계산] 차액={}, 수익률={}%", diff, currentRR);

            // 3. 유사 경매 조회
            log.debug("[유사경매DB조회시작] 기준경매ID={}, 원금={}, 남은일수={}",
                    auctionId, basePrincipal, baseDaysInt);
            List<Auction> similarAuctions = auctionRepository.findSimilarAuctionsByPrincipalAndDays(
                    auctionId, basePrincipal, baseDaysInt
            );
            log.debug("[유사경매DB조회결과] 유사경매 {}건 발견", similarAuctions.size());

            // 4. percentile 계산
            int rank = 0;
            for (int i = 0; i < similarAuctions.size(); i++) {
                if (currentRR.compareTo(similarAuctions.get(i).getReturnRate()) >= 0) {
                    rank = i + 1;
                }
            }
            int percentile = (int) Math.round((rank * 100.0) / similarAuctions.size());
            log.debug("[백분위계산] 순위={}/{}, 백분위={}%", rank, similarAuctions.size(), percentile);

            TargetAuctionResponseDTO targetAuctionResponseDTO = TargetAuctionResponseDTO.builder()
                    .auctionId(targetAuction.getAuctionId())
                    .rp(basePrincipal)
                    .rd(baseDaysInt)
                    .rr(currentRR)
                    .percentile(percentile)
                    .build();

            List<ComparisonAuctionResponseDTO> comparisonList = similarAuctions.stream()
                    .map(a -> {
                        ComparisonAuctionResponseDTO dto = ComparisonAuctionResponseDTO.builder()
                                .auctionId(a.getAuctionId())
                                .rp(a.getRemainPrincipal())
                                .rd(a.getRemainRepaymentDate())
                                .rr(a.getReturnRate())
                                .build();
                        log.trace("[유사경매항목] 경매ID={}, 원금={}, 남은일수={}, 수익률={}%",
                                a.getAuctionId(), a.getRemainPrincipal(),
                                a.getRemainRepaymentDate(), a.getReturnRate());
                        return dto;
                    })
                    .toList();

            SimilarAuctionResponseDTO responseDTO = SimilarAuctionResponseDTO.builder()
                    .targetAuction(targetAuctionResponseDTO)
                    .comparisonAuctions(comparisonList)
                    .build();

            log.info("[유사경매검색완료] 경매ID={}, 유사경매 {}건", auctionId, comparisonList.size());
            return responseDTO;

        } catch (Exception e) {
            log.error("[유사경매검색실패] 경매ID={}, 오류: {}", auctionId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR);
        }
    }

    public List<ContractEventDTO> getAuctionEvents(Integer auctionId) {
        log.info("[경매이벤트조회시작] 경매ID={}", auctionId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    log.error("[경매이벤트조회실패] 경매ID={}를 찾을 수 없음", auctionId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
                });
        log.debug("[경매이벤트조회] 경매ID={}, 토큰ID={} 이벤트 조회", auctionId, auction.getTokenId());

        List<ContractEventDTO> events = eventService.getEventList(auction.getTokenId());
        log.info("[경매이벤트조회완료] 경매ID={}, 이벤트 {}건 조회됨", auctionId, events.size());

        // 이벤트 정보 로깅 (많을 수 있으므로 TRACE 레벨에 기록)
        if (log.isTraceEnabled()) {
            events.forEach(event ->
                    log.trace("[경매이벤트상세] 경매ID={}, 이벤트유형={}, 이벤트시간={}, getTo={}, getFrom={}, getIntAmt={}",
                            auctionId, event.getEventType(), event.getTimestamp(), event.getTo(), event.getFrom(), event.getIntAmt())
            );
        }

        return events;
    }
}