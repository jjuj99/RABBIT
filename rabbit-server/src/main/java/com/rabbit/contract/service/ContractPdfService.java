package com.rabbit.contract.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.pdf.CommonPdfService;
import com.rabbit.global.service.ContractEncryptionService;
import com.rabbit.user.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 차용증 계약서 PDF 생성 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPdfService {

    private final CommonPdfService commonPdfService;
    private final WalletService walletService;
    private final ContractEncryptionService encryptionService; // 계약별 고유 키 암호화 서비스

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    /**
     * 계약 상세 정보를 바탕으로 차용증 PDF 생성 (원본 개인정보 포함)
     * @param contract 계약 엔티티
     * @return PDF 파일 바이트 배열
     */
    public byte[] generateContractPdf(Contract contract) {
        log.debug("차용증 PDF 생성: 계약 ID = {}", contract.getContractId());

        String title = "전자 차용증 (계약번호: " + contract.getContractId() + ")";

        return commonPdfService.generatePdf(title, (document, font, boldFont) -> {
            // 문서 제목
            addDocumentTitle(document, "전자 차용증", boldFont);

            // 계약 당사자 정보
            addContractParties(document, contract, boldFont);

            // 대출 주요 정보
            addLoanDetails(document, contract, boldFont);

            // 상환 조건
            addRepaymentTerms(document, contract, boldFont);

            // 약관 및 계약 조항
            addContractTerms(document, contract, boldFont);

            // 서명란
            addSignatureSection(document, contract, boldFont);
        });
    }

    /**
     * 계약 상세 정보를 바탕으로 개인정보가 암호화된 차용증 PDF 생성 (IPFS 업로드용)
     * @param contract 계약 엔티티
     * @return 암호화된 PDF 파일 바이트 배열
     */
    public byte[] generateEncryptedContractPdf(Contract contract) {
        log.debug("암호화된 차용증 PDF 생성: 계약 ID = {}", contract.getContractId());

        // 계약에 대한 새 암호화 키 생성 (아직 키가 없는 경우)
        if (contract.getEncryptedContractKey() == null) {
            // 새 키 생성 및 저장
            encryptionService.generateAndStoreKeyForContract(contract);
            log.info("계약 ID {}에 대한 암호화 키 새로 생성됨", contract.getContractId());
        }

        String title = "전자 차용증 (계약번호: " + contract.getContractId() + ")";

        return commonPdfService.generatePdf(title, (document, font, boldFont) -> {
            // 문서 제목
            addDocumentTitle(document, "전자 차용증", boldFont);

            // 계약 당사자 정보 (암호화 적용)
            addEncryptedContractParties(document, contract, boldFont);

            // 대출 주요 정보
            addLoanDetails(document, contract, boldFont);

            // 상환 조건
            addRepaymentTerms(document, contract, boldFont);

            // 약관 및 계약 조항
            addContractTerms(document, contract, boldFont);

            // 서명란 (암호화 적용)
            addEncryptedSignatureSection(document, contract, boldFont);
        });
    }

    /**
     * 문서 제목 추가
     */
    private void addDocumentTitle(Document document, String title, PdfFont boldFont) {
        Paragraph titleParagraph = new Paragraph(title)
                .setFontSize(20)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titleParagraph);
    }

    /**
     * 계약 당사자 정보 섹션 추가 (원본 정보)
     */
    private void addContractParties(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("1. 계약 당사자")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table partiesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 정보
        Map<String, String> creditorInfo = new HashMap<>();
        creditorInfo.put("이름", contract.getCreditor().getNickname());
        creditorInfo.put("이메일", contract.getCreditor().getEmail());
        creditorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getCreditor()));

        // 채무자 정보
        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("이름", contract.getDebtor().getNickname());
        debtorInfo.put("이메일", contract.getDebtor().getEmail());
        debtorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getDebtor()));

        addPersonInfoSection(partiesTable, "채권자 (대출해주는 사람)", creditorInfo, boldFont);
        addPersonInfoSection(partiesTable, "채무자 (대출받는 사람)", debtorInfo, boldFont);

        document.add(partiesTable);
        document.add(new Paragraph().setMarginBottom(15));
    }

    /**
     * 계약 당사자 정보 섹션 추가 (암호화 적용)
     */
    private void addEncryptedContractParties(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("1. 계약 당사자")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table partiesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 정보 (암호화 적용)
        Map<String, String> creditorInfo = new HashMap<>();
        creditorInfo.put("이름", encryptionService.encryptWithPrefix(contract, contract.getCreditor().getNickname()));
        creditorInfo.put("이메일", encryptionService.encryptWithPrefix(contract, contract.getCreditor().getEmail()));
        creditorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getCreditor())); // 지갑 주소는 암호화하지 않음

        // 채무자 정보 (암호화 적용)
        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("이름", encryptionService.encryptWithPrefix(contract, contract.getDebtor().getNickname()));
        debtorInfo.put("이메일", encryptionService.encryptWithPrefix(contract, contract.getDebtor().getEmail()));
        debtorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getDebtor())); // 지갑 주소는 암호화하지 않음

        addPersonInfoSection(partiesTable, "채권자 (대출해주는 사람)", creditorInfo, boldFont);
        addPersonInfoSection(partiesTable, "채무자 (대출받는 사람)", debtorInfo, boldFont);

        document.add(partiesTable);
        document.add(new Paragraph().setMarginBottom(15));
    }

    /**
     * 계약 당사자 정보 표시를 위한 테이블 생성
     */
    private void addPersonInfoSection(Table parentTable, String title, Map<String, String> info, PdfFont boldFont) {
        Paragraph titleParagraph = new Paragraph(title)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.CENTER);

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        info.forEach((key, value) -> {
            Cell keyCell = new Cell();
            keyCell.add(new Paragraph(key).setFont(boldFont));
            keyCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            infoTable.addCell(keyCell);

            infoTable.addCell(value != null ? value : "");
        });

        parentTable.addCell(
                new Paragraph().add(titleParagraph).add(new Paragraph().setMarginBottom(5)).add(infoTable)
        );
    }

    /**
     * 대출 주요 정보 섹션 추가
     */
    private void addLoanDetails(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("2. 대출 정보")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Map<String, String> loanDetails = new HashMap<>();
        loanDetails.put("대출 금액", formatAmount(contract.getLoanAmount()) + " 원");
        loanDetails.put("연 이자율", contract.getInterestRate() + "%");
        loanDetails.put("계약일", formatDate(contract.getContractDate()));
        loanDetails.put("만기일", formatDate(contract.getMaturityDate()));
        loanDetails.put("대출 기간", contract.getLoanTerm() + "개월");

        commonPdfService.addKeyValueTable(document, loanDetails, boldFont);
        document.add(new Paragraph().setMarginBottom(10));
    }

    /**
     * 상환 조건 섹션 추가
     */
    private void addRepaymentTerms(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("3. 상환 조건")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Map<String, String> repaymentTerms = new HashMap<>();
        repaymentTerms.put("상환 방식", contract.getRepaymentType().getCodeName());

        if (contract.getMonthlyPaymentDate() != null) {
            repaymentTerms.put("월 납입일", "매월 " + contract.getMonthlyPaymentDate() + "일");
        }

        repaymentTerms.put("연체 이자율", contract.getDefaultInterestRate() + "%");
        repaymentTerms.put("기한이익상실 연체 횟수", contract.getDefaultCount() + "회");
        repaymentTerms.put("납부 유예 일수", contract.getGraceLineDays() + "일");
        repaymentTerms.put("중도 상환 가능 여부", contract.getEarlyPayment() ? "가능" : "불가능");

        if (contract.getEarlyPayment() && contract.getPrepaymentInterestRate() != null) {
            repaymentTerms.put("중도상환 수수료율", contract.getPrepaymentInterestRate() + "%");
        }

        repaymentTerms.put("차용증 양도 가능 여부", contract.getPromissoryNoteTransferabilityFlag() ? "가능" : "불가능");

        commonPdfService.addKeyValueTable(document, repaymentTerms, boldFont);
        document.add(new Paragraph().setMarginBottom(10));
    }

    /**
     * 계약 조항 섹션 추가
     */
    private void addContractTerms(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("4. 계약 조항")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        // 기본 계약 조항
        String defaultTerms = "1. 채무자는 약정된 만기일에 원금과 이자를 합한 금액을 채권자에게 상환한다.\n" +
                "2. 상환이 지연될 경우 지연된 날로부터 연체이자가 적용된다.\n" +
                "3. 연속적인 상환 불이행 시 기한이익이 상실되어 잔여 대출금 전액을 즉시 상환해야 한다.\n" +
                "4. 본 계약은 블록체인 상에 기록되며 NFT로 발행된다.";

        Paragraph defaultTermsParagraph = new Paragraph(defaultTerms);
        document.add(defaultTermsParagraph);

        // 추가 계약 조항 (있는 경우)
        if (contract.getContractTerms() != null && !contract.getContractTerms().isEmpty()) {
            Paragraph additionalTermsTitle = new Paragraph("추가 계약 조항")
                    .setFont(boldFont)
                    .setMarginTop(10)
                    .setMarginBottom(5);
            document.add(additionalTermsTitle);

            Paragraph additionalTerms = new Paragraph(contract.getContractTerms());
            document.add(additionalTerms);
        }

        document.add(new Paragraph().setMarginBottom(15));
    }

    /**
     * 서명란 섹션 추가 (원본 정보)
     */
    private void addSignatureSection(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("5. 서명")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Paragraph signatureParagraph = new Paragraph(
                "본 전자 차용증은 블록체인 상의 스마트 계약으로 체결되었으며, " +
                        "각 당사자의 전자 서명(지갑 주소를 통한 트랜잭션)으로 효력이 발생합니다.")
                .setMarginBottom(15);
        document.add(signatureParagraph);

        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 서명 정보
        Paragraph creditorSignature = new Paragraph();
        Text creditorTitle = new Text("채권자: " + contract.getCreditor().getNickname() + "\n");
        creditorTitle.setFont(boldFont);
        creditorSignature.add(creditorTitle);
        creditorSignature.add("지갑 주소: " + walletService.getUserPrimaryWalletAddress(contract.getCreditor()) + "\n");
        creditorSignature.add("서명일시: " + formatDateTime(contract.getUpdatedAt()));

        // 채무자 서명 정보
        Paragraph debtorSignature = new Paragraph();
        Text debtorTitle = new Text("채무자: " + contract.getDebtor().getNickname() + "\n");
        debtorTitle.setFont(boldFont);
        debtorSignature.add(debtorTitle);
        debtorSignature.add("지갑 주소: " + walletService.getUserPrimaryWalletAddress(contract.getDebtor()) + "\n");
        debtorSignature.add("서명일시: " + formatDateTime(contract.getCreatedAt()));

        signatureTable.addCell(creditorSignature);
        signatureTable.addCell(debtorSignature);

        document.add(signatureTable);

        // NFT 정보 추가 (토큰 ID가 있는 경우)
        if (contract.getTokenId() != null) {
            document.add(new Paragraph().setMarginBottom(15));

            Paragraph nftInfo = new Paragraph();
            Text nftTitle = new Text("NFT 정보:\n");
            nftTitle.setFont(boldFont);
            nftInfo.add(nftTitle);
            nftInfo.add("토큰 ID: " + contract.getTokenId() + "\n");

            document.add(nftInfo);
        }
    }

    /**
     * 서명란 섹션 추가 (암호화 적용)
     */
    private void addEncryptedSignatureSection(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("5. 서명")
                .setFontSize(14)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Paragraph signatureParagraph = new Paragraph(
                "본 전자 차용증은 블록체인 상의 스마트 계약으로 체결되었으며, " +
                        "각 당사자의 전자 서명(지갑 주소를 통한 트랜잭션)으로 효력이 발생합니다.")
                .setMarginBottom(15);
        document.add(signatureParagraph);

        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 서명 정보 (암호화 적용)
        Paragraph creditorSignature = new Paragraph();
        Text creditorTitle = new Text("채권자: " + encryptionService.encryptWithPrefix(contract, contract.getCreditor().getNickname()) + "\n");
        creditorTitle.setFont(boldFont);
        creditorSignature.add(creditorTitle);
        creditorSignature.add("지갑 주소: " + walletService.getUserPrimaryWalletAddress(contract.getCreditor()) + "\n");
        creditorSignature.add("서명일시: " + formatDateTime(contract.getUpdatedAt()));

        // 채무자 서명 정보 (암호화 적용)
        Paragraph debtorSignature = new Paragraph();
        Text debtorTitle = new Text("채무자: " + encryptionService.encryptWithPrefix(contract, contract.getDebtor().getNickname()) + "\n");
        debtorTitle.setFont(boldFont);
        debtorSignature.add(debtorTitle);
        debtorSignature.add("지갑 주소: " + walletService.getUserPrimaryWalletAddress(contract.getDebtor()) + "\n");
        debtorSignature.add("서명일시: " + formatDateTime(contract.getCreatedAt()));

        signatureTable.addCell(creditorSignature);
        signatureTable.addCell(debtorSignature);

        document.add(signatureTable);

        // NFT 정보 추가 (토큰 ID가 있는 경우)
        if (contract.getTokenId() != null) {
            document.add(new Paragraph().setMarginBottom(15));

            Paragraph nftInfo = new Paragraph();
            Text nftTitle = new Text("NFT 정보:\n");
            nftTitle.setFont(boldFont);
            nftInfo.add(nftTitle);
            nftInfo.add("토큰 ID: " + contract.getTokenId() + "\n");

            document.add(nftInfo);
        }
    }

    /**
     * NFT 메타데이터용 암호화
     * @param contract 계약 엔티티
     * @param plainText 암호화할 텍스트
     * @return 암호화된 텍스트 (프리픽스 포함)
     */
    public String encryptForMetadata(Contract contract, String plainText) {
        return encryptionService.encryptWithPrefix(contract, plainText);
    }

    // 유틸리티 메서드들

    /**
     * 금액 포맷팅 (천 단위 콤마)
     */
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "";

        return String.format("%,d", amount.setScale(0, RoundingMode.DOWN).longValue());
    }

    /**
     * 날짜 포맷팅 (yyyy년 MM월 dd일)
     */
    private String formatDate(ZonedDateTime dateTime) {
        if (dateTime == null) return "";

        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * 날짜 시간 포맷팅 (yyyy년 MM월 dd일 HH시 mm분)
     */
    private String formatDateTime(ZonedDateTime dateTime) {
        if (dateTime == null) return "";

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
    }
}