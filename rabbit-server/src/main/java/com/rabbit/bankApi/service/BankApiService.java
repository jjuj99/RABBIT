package com.rabbit.bankApi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.bankApi.domain.api.Header.HeaderUtil;
import com.rabbit.bankApi.domain.api.request.*;
import com.rabbit.bankApi.domain.api.response.ErrorApiResponse;
import com.rabbit.bankApi.domain.dto.request.*;
import com.rabbit.bankApi.domain.dto.response.*;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiService {

    private static final String ISSUE_KEY_URL = "/edu/app/issuedApiKey";
    private static final String REISSUE_KEY_URL = "/edu/app/reIssuedApiKey";
    private static final String CREATE_MEMBER_URL = "/member";
    private static final String SEARCH_MEMBER_URL = "/member/search";
    private static final String CREATE_DEMAND_DEPOSIT_URL = "/edu/demandDeposit/createDemandDeposit";
    private static final String CREATE_DEMAND_ACCOUNT_URL = "/edu/demandDeposit/createDemandDepositAccount";
    private static final String INQUIRE_TRANSACTION_URL = "/edu/demandDeposit/inquireTransactionHistoryList";
    private static final String UPDATE_DEPOSIT_URL = "/edu/demandDeposit/updateDemandDepositAccountDeposit";
    private static final String MY_CREDIT_URL = "/edu/loan/inquireMyCreditRating";
    private static final String OPEN_ACCOUNT_URL = "/edu/accountAuth/openAccountAuth";
    private static final String CHECK_ACCOUNT_URL = "/edu/accountAuth/checkAuthCode";

    private static final String CREATE_DEMAND_DEPOSIT_NAME = "createDemandDeposit";
    private static final String CREATE_DEMAND_ACCOUNT_NAME = "createDemandDepositAccount";
    private static final String INQUIRE_TRANSACTION_NAME= "inquireTransactionHistoryList";
    private static final String UPDATE_DEPOSIT_NAME = "updateDemandDepositAccountDeposit";
    private static final String MY_CREDIT_NAME = "inquireMyCreditRating";
    private static final String OPEN_ACCOUNT_NAME = "openAccountAuth";
    private static final String CHECK_ACCOUNT_NAME = "checkAuthCode";

    private static final String ACCOUNT_UNIQUE_NO = "001-1-f99815e056e341";
    private static final String AUTH_TEXT = "RABBIT";

    @Value("${ssafy.api-key}")
    public String apiKey;

    private final WebClient webClient;

    public Mono<ApiKeyResponseDTO> issueApiKey(String email){
        ApiKeyRequestDTO request = ApiKeyRequestDTO.builder()
                .managerId(email)
                .build();

        return webClient.post()
                .uri(ISSUE_KEY_URL)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiKeyResponseDTO.class);
    }

    public Mono<ApiKeyResponseDTO> reIssueApiKey(String email){
        ApiKeyRequestDTO request = ApiKeyRequestDTO.builder()
                .managerId(email)
                .build();

        return webClient.post()
                .uri(REISSUE_KEY_URL)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiKeyResponseDTO.class);
    }

    public Mono<MemberResponseDTO> createMember(MemberRequestDTO request) {
        MemberApiRequest client = MemberApiRequest.builder()
                .userId(request.getUserId())
                .apiKey(apiKey)
                .build();

        return webClient.post()
                .uri(CREATE_MEMBER_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(MemberResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<MemberResponseDTO> searchMember(MemberRequestDTO request) {
        MemberApiRequest client = MemberApiRequest.builder()
                .userId(request.getUserId())
                .apiKey(apiKey)
                .build();

        return webClient.post()
                .uri(SEARCH_MEMBER_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(MemberResponseDTO.class)
                .onErrorResume( error -> {
                    // 에러 응답 파싱 시도
                    return WebClientResponseException.class.isAssignableFrom(error.getClass())
                            ? Mono.defer(() -> {
                        WebClientResponseException ex = (WebClientResponseException) error;
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ErrorApiResponse errorResponse = mapper.readValue(ex.getResponseBodyAsString(), ErrorApiResponse.class);

                            // E4001이면 null 반환
//                            if ("E4001".equals(errorResponse.getResponseCode())) {
//                                return Mono.empty();
//                            }

                            if ("E4003".equals(errorResponse.getResponseCode())) {
                                return Mono.empty();
                            }

                            return Mono.error(new BusinessException(
                                    ErrorCode.BUSINESS_LOGIC_ERROR,
                                    errorResponse.getResponseMessage())
                            );
                        } catch (Exception e) {
                            log.error("에러 응답 파싱 중 오류 발생", e);
                            return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                                    "외부 API 호출 중 알 수 없는 오류가 발생했습니다."));
                        }
                    })
                            : Mono.error(error); // 다른 종류의 예외는 그대로 전달
                });
    }

    public Mono<CreateDemandDepositResponseDTO> createDemandDeposit(CreateDemandDepositRequestDTO request) {
        CreateDemandDepositApiRequest client = CreateDemandDepositApiRequest.builder()
                .header(HeaderUtil.createHeader(CREATE_DEMAND_DEPOSIT_NAME, apiKey))
                .bankCode(request.getBankCode())
                .accountName(request.getAccountName())
                .accountDescription(request.getAccountDescription())
                .build();

        return webClient.post()
                .uri(CREATE_DEMAND_DEPOSIT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(CreateDemandDepositResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<CreateDemandAccountResponseDTO> createDemandAccount(CreateDemandAccountRequestDTO request) {
        CreateDemandAccountApiRequest client = CreateDemandAccountApiRequest.builder()
                .header(HeaderUtil.createHeader(CREATE_DEMAND_ACCOUNT_NAME, apiKey, request.getUserKey()))
                .accountTypeUniqueNo(ACCOUNT_UNIQUE_NO)
                .build();

        return webClient.post()
                .uri(CREATE_DEMAND_ACCOUNT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(CreateDemandAccountResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<InquireTransactionResponseDTO> inquireTransaction(InquireTransactionRequestDTO request) {
        LocalDateTime now = LocalDateTime.now();
        String endDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        InquireTransactionApiRequest client = InquireTransactionApiRequest.builder()
                .header(HeaderUtil.createHeader(INQUIRE_TRANSACTION_NAME, apiKey, request.getUserKey()))
                .accountNo(request.getAccountNo())
                .startDate(request.getStartDate())
                .endDate(endDate)
                .transactionType(request.getTransactionType())
                .orderByType(request.getOrderByType())
                .build();

        return webClient.post()
                .uri(INQUIRE_TRANSACTION_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(InquireTransactionResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<UpdateDepositResponseDTO> updateDeposit(UpdateDepositRequestDTO request) {
        UpdateDepositApiRequest client = UpdateDepositApiRequest.builder()
                .header(HeaderUtil.createHeader(UPDATE_DEPOSIT_NAME, apiKey, request.getUserKey()))
                .accountNo(request.getAccountNo())
                .transactionBalance(request.getTransactionBalance())
                .transactionSummary(request.getTransactionSummary())
                .build();

        return webClient.post()
                .uri(UPDATE_DEPOSIT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(UpdateDepositResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<MyCreditResponseDTO> myCredit(UserKeyRequestDTO request) {
        MyCreditApiRequest client = MyCreditApiRequest.builder()
                .header(HeaderUtil.createHeader(MY_CREDIT_NAME, apiKey, request.getUserKey()))
                .build();

        return webClient.post()
                .uri(MY_CREDIT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(MyCreditResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<OpenAccountResponseDTO> openAccount(OpenAccountRequestDTO request) {
        OpenAccountApiRequest client = OpenAccountApiRequest.builder()
                .header(HeaderUtil.createHeader(OPEN_ACCOUNT_NAME, apiKey, request.getUserKey()))
                .accountNo(request.getAccountNo())
                .authText(AUTH_TEXT)
                .build();

        return webClient.post()
                .uri(OPEN_ACCOUNT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(OpenAccountResponseDTO.class)
                .onErrorResume(error -> {
                    log.error("API 요청 중 오류 발생", error);
                    return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "외부 API 호출 중 오류가 발생했습니다."));
                });
    }

    public Mono<CheckAccountResponseDTO> checkAccount(CheckAccountRequestDTO request) {
        CheckAccountApiRequest client = CheckAccountApiRequest.builder()
                .header(HeaderUtil.createHeader(CHECK_ACCOUNT_NAME, apiKey, request.getUserKey()))
                .accountNo(request.getAccountNo())
                .authText(AUTH_TEXT)
                .authCode(request.getAuthCode())
                .build();

        return webClient.post()
                .uri(CHECK_ACCOUNT_URL)
                .bodyValue(client)
                .retrieve()
                .bodyToMono(CheckAccountResponseDTO.class)
                .onErrorResume(error -> {
                    // 에러 응답 파싱 시도
                    return WebClientResponseException.class.isAssignableFrom(error.getClass())
                            ? Mono.defer(() -> {
                        WebClientResponseException ex = (WebClientResponseException) error;
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ErrorApiResponse errorResponse = mapper.readValue(ex.getResponseBodyAsString(), ErrorApiResponse.class);

                            // E1090이면 null 반환
                            if ("A1090".equals(errorResponse.getResponseCode())) {
                                return Mono.empty();
                            }

                            return Mono.error(new BusinessException(
                                    ErrorCode.BUSINESS_LOGIC_ERROR,
                                    errorResponse.getResponseMessage())
                            );
                        } catch (Exception e) {
                            log.error("에러 응답 파싱 중 오류 발생", e);
                            return Mono.error(new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                                    "외부 API 호출 중 알 수 없는 오류가 발생했습니다."));
                        }
                    })
                            : Mono.error(error); // 다른 종류의 예외는 그대로 전달
                });
    }
}
