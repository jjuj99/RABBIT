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
import com.rabbit.blockchain.service.*;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.contract.service.ContractService;
import com.rabbit.blockchain.service.PromissoryNoteAuctionService;
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
import com.rabbit.promissorynote.service.PromissoryNoteBusinessService;
import com.rabbit.sse.service.SseEventPublisher;
import com.rabbit.user.domain.dto.response.ProfileInfoResponseDTO;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.MetamaskWalletRepository;
import com.rabbit.user.service.UserService;
import com.rabbit.user.domain.entity.MetamaskWallet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

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
    private final PromissoryNoteBusinessService promissoryNoteBusinessService;

    // 코드 타입 상수 정의
    private static final String AUCTION_STATUS = SysCommonCodes.Auction.values()[0].getCodeType();
    private static final String BID_STATUS = SysCommonCodes.Bid.values()[0].getCodeType();

    public AuctionIdDTO addAuction(@Valid AuctionRequestDTO auctionRequest, Integer userId) {
        String tokenId = auctionRequest.getTokenId().toString();
        PromissoryNoteEntity promissoryNote = promissoryNoteRepository.findById(Long.parseLong(tokenId))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 tokenId의 계약이 없습니다."));

        String account = metamaskWalletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND, "사용자의 주 지갑을 찾을 수 없습니다"));

        if(!promissoryNote.getCreditorWalletAddress().equals(account)){
           throw new BusinessException(ErrorCode.UNAUTHORIZED, "해당 NFT 경매의 권한이 없습니다.");
        }

        //이미 경매가 진행중인지 확인
        auctionRepository.findByTokenIdAndAuctionStatus(auctionRequest.getTokenId(), SysCommonCodes.Auction.ING)
                .ifPresent(auction ->{
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "해당 NFT는 이미 경매가 진행 중입니다.");
                });

        User assignor = userService.findById(userId);

        Auction auction = Auction.builder()
                .assignor(assignor)  //아직 임의로 설정해둠
                .minimumBid(auctionRequest.getMinimumBid())
                .endDate(auctionRequest.getEndDate())
                .tokenId(auctionRequest.getTokenId())
                .auctionStatus(SysCommonCodes.Auction.ING)
                .sellerSign(null)
                .createdAt(ZonedDateTime.now())
                .build();

        Auction savedAuction = auctionRepository.save(auction);

        auctionScheduler.scheduleAuctionEnd(savedAuction.getAuctionId(), savedAuction.getEndDate());

        return AuctionIdDTO.builder()
                .auctionId(savedAuction.getAuctionId())
                .build();
    }

    public PageResponseDTO<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO request, Pageable pageable) {
        log.info("[AuctionService] 가격 조건 요청: minPrice={}, maxPrice={}", request.getMinPrice(), request.getMaxPrice());

        Page<AuctionResponseDTO> result = auctionRepository.searchAuctions(request, pageable);

        //블록체인 읽어와 다른 조건 필터링 구현 필요
        List<AuctionResponseDTO> fullList = result.getContent().stream()
                .map(dto -> {
                    try {
                        log.info("[Auction] 경매 목록을 위한 정보 호출 auctionId={}, tokenId={}", dto.getAuctionId(), dto.getTokenId());

                        // 블록체인 메타데이터 조회
                        PromissoryNote.PromissoryMetadata metadata = promissoryNoteService.getPromissoryMetadata(dto.getTokenId());

                        // 상환 정보 조회 (연체 횟수, 남은 원금)
                        RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(dto.getTokenId());

                        // 채무자 조회 및 신용 점수 조회
                        User debtor = contractService.getDebtorByTokenId(dto.getTokenId());
                        String creditScore = bankService.getCreditScore(debtor.getUserId());

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

                        return AuctionResponseDTO.builder()
                                .auctionId(dto.getAuctionId())
                                .price(dto.getPrice())
                                .endDate(dto.getEndDate())
                                .createdAt(dto.getCreatedAt())
                                .ir(ir)
                                .tokenId(dto.getTokenId())
                                .repayType(SysCommonCodes.Repayment.fromCode(metadata.repayType).getCodeName())  // 한글 상환 방식
                                .totalAmount(totalAmount.longValue()) // 총 수취액 로직 필요 시 계산 함수 넣기
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
                        log.warn("[리스트 변환 오류] auctionId={}", dto.getAuctionId(), e);
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
                    } else if (request.getMatStart() != null && request.getMatEnd() != null) {
                        matCheck = !dto.getMatDt().isBefore(request.getMatStart()) && !dto.getMatDt().isAfter(request.getMatEnd());
                    }

                    return irCheck && repayCheck && matCheck;
                })
                .toList();

        // 블록체인 필터링 후 다시한번 페이징
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

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

    public List<AuctionMyListResponseDTO> myAuctionList(Integer userId){
        // userId가 동일한 경매만 필터링
        List<Auction> auctions = auctionRepository.findAll()
                .stream()
                .filter(auction -> auction.getAssignor().getUserId().equals(userId))
                .toList();

        return auctions.stream()
                .map(auction -> {
                    String nftImageUrl = promissoryNoteRepository.findNftImageByTokenId(auction.getTokenId())
                            .orElse(null);

                    return AuctionMyListResponseDTO.builder()
                            .auctionId(auction.getAuctionId())
                            .price(auction.getPrice())
                            .endDate(auction.getEndDate())
                            .tokenId(auction.getTokenId())
                            .nftImageUrl(nftImageUrl)
                            .auctionStatus(auction.getAuctionStatus())
                            .build();
                })
                .toList();
    }

    public void cancelAuction(@Valid Integer auctionId, Integer userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        // 내 auction이 아니면 취소 불가
        if(!auction.getAssignor().getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "경매 취소 권한이 없습니다.");
        }

        //입찰자 존재시 취소 불가
        boolean hasBids=bidRepository.existsByAuction(auction);
        if(hasBids){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰자가 존재해 경매를 취소할 수 없습니다.");
        }

        //블록체인에서 경매 취소
        try {
            promissoryNoteAuctionService.cancelAuction(auction.getTokenId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "경매 취소 중 오류가 발생했습니다.");
        }

        //cancel로 상태 변경
        auction.setAuctionStatus(SysCommonCodes.Auction.CANCELED);

        auctionRepository.save(auction);
    }

    public void deleteAuction(@Valid Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        auctionRepository.delete(auction);
    }

    public PageResponseDTO<MyAuctionResponseDTO> getMyBidAuctions(Integer userId, Pageable pageable) {
        Page<MyAuctionResponseDTO> result = auctionRepository.getMyBidAuction(userId, pageable);

        // 국제화된 상태명 설정
        // page 처리 된 곳이기 때문에 매번 국제화를 불러와도 성능 차이 미미함 (10 - 50개 정도이기 때문)
        // but 엑셀 저장과 같이 한번에 수만건 처리해야하는 batch 작업이라면 국제화한 code name 을 미리 계산해서 사용하는 것이 성능에 좋음
        result.getContent().forEach(dto -> {
            if (dto.getAuctionStatus() != null) {
                dto.setAuctionStatusName(sysCommonCodeService.getCodeName(
                        AUCTION_STATUS, dto.getAuctionStatus().getCode()));
            }
            if (dto.getBidStatus() != null) {
                dto.setBidStatusName(sysCommonCodeService.getCodeName(
                        BID_STATUS, dto.getBidStatus()));
            }

            if (dto.getTokenId() != null) {
                Optional<String> imageOpt = promissoryNoteRepository.findNftImageByTokenId(dto.getTokenId());
                imageOpt.ifPresent(dto::setNftImage);
            }
        });

        return PageResponseDTO.<MyAuctionResponseDTO>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    public AuctionDetailResponseDTO getAuctionDetail(Integer auctionId, Integer userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        User debtor = contractService.getDebtorByTokenId(auction.getTokenId());
//        String creditScore = bankService.getCreditScore(debtor.getUserId());
        String creditScore = "B";

        //블록체인에서 직접 읽어온 값 추가 필요
        try {
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(auction.getTokenId());
            RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(auction.getTokenId());

            BigDecimal ir = new BigDecimal(promissoryMetadata.ir).divide(BigDecimal.valueOf(10000));
            BigDecimal dir = new BigDecimal(promissoryMetadata.dir).divide(BigDecimal.valueOf(10000));
            BigDecimal earlyPayFee = new BigDecimal(promissoryMetadata.earlyPayFee).divide(BigDecimal.valueOf(10000));

            // 만기수취액 계산
            BigDecimal totalAmount = loanUtil.calculateTotalRepaymentAmount(
                    new BigDecimal(repaymentInfo.remainingPrincipal),
                    ir,
                    repaymentInfo.remainingPayments.intValue(),
                    SysCommonCodes.Repayment.toCalculationType(promissoryMetadata.repayType),
                    LoanUtil.RoundingStrategy.HALF_UP,
                    LoanUtil.TruncationStrategy.WON,
                    LoanUtil.LegalLimits.getDefaultLimits()
            );

            Long curPrice = auction.getPrice()==null? auction.getMinimumBid(): auction.getPrice();

            String pdfUrl = promissoryNoteBusinessService.getPromissoryNotePdfUriByTokenId(auction.getTokenId());

            return AuctionDetailResponseDTO.builder()
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
                    .defCnt(repaymentInfo.overdueInfo.defCnt.intValue())   //연체 횟수
                    .endDate(auction.getEndDate())
                    .createdAt(auction.getCreatedAt())
                    .nftImageUrl(promissoryMetadata.nftImage)
                    .auctionStatus(auction.getAuctionStatus())
                    .mineFlag(auction.getAssignor().getUserId().equals(userId))
                    .pdfUrl(pdfUrl)
                    .build();
        } catch (Exception e) {
            log.error("[블록체인 오류] getPromissoryMetadata 실패", e);
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR);
        }
    }

    public void processAuctionEnd(Integer auctionId) {
        log.info("경매 종료 처리 시작 - auctionId={}", auctionId);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        if (auction.getAuctionStatus() != SysCommonCodes.Auction.ING) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "진행중인 경매가 아닙니다.");
        }

        List<Bid> bids = bidRepository.findAllByAuction_AuctionIdOrderByBidAmountDescCreatedAtAsc(auctionId);

        if (bids.isEmpty()) {   //낙찰자가 없는 경우
            auction.setAuctionStatus(SysCommonCodes.Auction.FAILED);

            try {
                // 경매 컨트랙트의 cancelAuction으로 경매 취소
                TransactionReceipt receipt = promissoryNoteAuctionService.cancelAuction(auction.getTokenId());

                // 트랜잭션 성공 여부 확인
                boolean isSuccess = "0x1".equals(receipt.getStatus());
                if (isSuccess) {
                    log.info("경매 취소 성공");
                } else {
                    log.error("경매 취소 실패. 트랜잭션 상태: {}", receipt.getStatus());
                }
            } catch (Exception e) {
                log.error("경매 취소 블록체인 오류: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.BLOCKCHAIN_AUCTION_CANCEL_FAIL, "블록체인에서 경매 취소에 실패했습니다.");
            }

            //양도자에게 알림
            notificationService.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(auction.getAssignor().getUserId())
                            .type(SysCommonCodes.NotificationType.AUCTION_FAILED)
                            .relatedId(auctionId)
                            .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                            .build()
            );
        }
        else {
            Bid winningBid = bids.get(0);   //최고가
            auction.updatePriceAndBidder(winningBid.getBidAmount(), winningBid.getUserId());
            auction.setAuctionStatus(SysCommonCodes.Auction.COMPLETED);

            // 낙찰 종료 트랜잭션 실행
            ProfileInfoResponseDTO grantor = userService.getProfileInfo(auction.getAssignor().getUserId());
            ProfileInfoResponseDTO grantee = userService.getProfileInfo(winningBid.getUserId());

            // 상환 정보 조회 (연체 횟수, 남은 원금)
            RepaymentInfo repaymentInfo;
            try {
                repaymentInfo = repaymentSchedulerService.getRepaymentInfo(auction.getTokenId());
            } catch (Exception e) {
                log.error("상환정보 조회 오류: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.BLOCKCHAIN_REPAYMENT_FAIL, "블록체인에서 상환 조회에 실패했습니다.");
            }

            // 채무자 정보 가져오기
            User debtor = contractService.getDebtorByTokenId(auction.getTokenId());
            MetamaskWallet debtorWallet = userService.getWalletByUserIdAndPrimaryFlagTrue(debtor.getUserId());

            // pdf 만들기
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

            // pdf pinata에 업로드하기
            String pdfFileName = "양도양수계약서_" + auction.getAuctionId() + ".pdf";
            String pdfUrl = pinataUploader.uploadContent(transferPdfBytes, pdfFileName, "application/pdf");

            auction.setContractIpfsUrl(pdfUrl);

            // 해시값 구하기
            String grantorHash = IntegrityHashUtil.generateIntegrityHash(grantor.getUserName(), grantor.getEmail(), grantor.getWalletAddress());
            String granteeHash = IntegrityHashUtil.generateIntegrityHash(grantee.getUserName(), grantee.getEmail(), grantee.getWalletAddress());

            // 부속 nft 발행
            try {
                // 메타데이터 생성
                PromissoryNoteAuction.AppendixMetadata metadata = new PromissoryNoteAuction.AppendixMetadata(
                        auction.getTokenId(),
                        auction.getSellerSign(),
                        grantor.getUserName(),
                        grantor.getUserName(),
                        grantorHash,
                        winningBid.getBidderSign(),
                        grantee.getUserName(),
                        grantee.getWalletAddress(),
                        granteeHash,
                        repaymentInfo.remainingPrincipal,
                        ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        pdfUrl
                );

                MetamaskWallet currentBidderWallet = userService.getWalletByUserIdAndPrimaryFlagTrue(winningBid.getUserId());

                promissoryNoteAuctionService.finalizeAuction(
                        auction.getTokenId(),
                        currentBidderWallet.getWalletAddress(),
                        BigInteger.valueOf(winningBid.getBidAmount()),
                        metadata
                );
            } catch (Exception e) {
                log.error("스마트컨트랙트 finalizeAuction 실패", e);
                throw new BusinessException(ErrorCode.BLOCKCHAIN_AUCTION_END_FAIL, "블록체인에서 경매 종료를 실패했습니다");
            }

            // 낙찰자에게 성공 알림
            notificationService.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(winningBid.getUserId())
                            .type(SysCommonCodes.NotificationType.AUCTION_SUCCESS)
                            .relatedId(auctionId)
                            .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                            .build()
            );

            User winner = userService.findById(winningBid.getUserId());
            extendedMailService.sendAuctionContractToAssignee(
                    winner.getEmail(),
                    winner.getUserName(),
                    auction.getTokenId(),
                    transferPdfBytes
            );

            // 양도자에게 전송 예정 알림
            notificationService.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(auction.getAssignor().getUserId())
                            .type(SysCommonCodes.NotificationType.AUCTION_TRANSFERRED)
                            .relatedId(auctionId)
                            .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                            .build()
            );

            User seller = userService.findById(auction.getAssignor().getUserId());
            extendedMailService.sendAuctionContractToAssignor(
                    seller.getEmail(),
                    seller.getUserName(),
                    auction.getTokenId(),
                    transferPdfBytes
            );

            // 채무자에게 알림 메일 전송
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

            extendedMailService.sendAuctionTransferNoticeEmail(
                    debtor.getEmail(),
                    debtor.getUserName(),
                    grantee.getUserName(),
                    noticePdfBytes,
                    transferPdfBytes,
                    auction.getAuctionId()
            );
        }

        auctionRepository.save(auction);

        //차용증 현재 채권자 정보 변경
        contractService.changeCreditorByTokenId(auction.getTokenId(), auction.getAssignor().getUserId());
    }

    public SimilarAuctionResponseDTO getSimilarAuctions(@Valid Integer auctionId) {
        Auction targetAuction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        // 2. 기준 값 추출
        // 남은 원금, 남은 상환일 조회 => nft에서 조회
        try {
            PromissoryNote.PromissoryMetadata metadata = promissoryNoteService.getPromissoryMetadata(BigInteger.valueOf(targetAuction.getAuctionId()));
            RepaymentInfo repaymentInfo = repaymentSchedulerService.getRepaymentInfo(targetAuction.getTokenId());

            Long basePrincipal = repaymentInfo.remainingPrincipal.longValue();
            long baseDays = ChronoUnit.DAYS.between(LocalDate.now(),  LocalDate.parse(metadata.matDt));
            baseDays = Math.max(baseDays, 0);

            Integer baseDaysInt = (int) baseDays;

            // 현재 수익률 (기대가격-현재가격)/100?
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

            // 현재 가격
            BigDecimal currentPrice = BigDecimal.valueOf(
                    targetAuction.getPrice() != null ? targetAuction.getPrice() : targetAuction.getMinimumBid()
            );

            // (totalAmount - currentPrice) / currentPrice * 100
            BigDecimal diff = totalAmount.subtract(currentPrice);
            BigDecimal rate = diff.divide(currentPrice, 6, RoundingMode.HALF_UP); // 소수점 6자리까지
            BigDecimal currentRR = rate.multiply(BigDecimal.valueOf(100)); // 퍼센트로 변환

            // 3. 유사 경매 조회
            List<Auction> similarAuctions = auctionRepository.findSimilarAuctionsByPrincipalAndDays(
                    auctionId, basePrincipal, baseDaysInt
            );

            // 4. percentile 계산
            int rank = 0;
            for (int i = 0; i < similarAuctions.size(); i++) {
                if (currentRR.compareTo(similarAuctions.get(i).getReturnRate()) >= 0) {
                    rank = i + 1;
                }
            }
            int percentile = (int) Math.round((rank * 100.0) / similarAuctions.size());

            TargetAuctionResponseDTO targetAuctionResponseDTO = TargetAuctionResponseDTO.builder()
                    .auctionId(targetAuction.getAuctionId())
                    .rp(basePrincipal)
                    .rd(baseDaysInt)
                    .rr(currentRR)
                    .percentile(percentile)
                    .build();

            List<ComparisonAuctionResponseDTO> comparisonList = similarAuctions.stream()
                    .map(a -> ComparisonAuctionResponseDTO.builder()
                            .auctionId(a.getAuctionId())
                            .rp(a.getRemainPrincipal())
                            .rd(a.getRemainRepaymentDate())
                            .rr(a.getReturnRate())
                            .build())
                    .toList();

            return SimilarAuctionResponseDTO.builder()
                    .targetAuction(targetAuctionResponseDTO)
                    .comparisonAuctions(comparisonList)
                    .build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR);
        }
    }

    public List<ContractEventDTO> getAuctionEvents(@Valid Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        List<ContractEventDTO> events = eventService.getEventList(auction.getTokenId());

        return events;
    }
}
