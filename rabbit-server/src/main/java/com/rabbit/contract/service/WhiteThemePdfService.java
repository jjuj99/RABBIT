package com.rabbit.contract.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.pdf.PdfEventHandler;
import com.rabbit.global.service.ContractEncryptionService;
import com.rabbit.user.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 화이트 테마 차용증 계약서 PDF 생성 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhiteThemePdfService {

    private final WalletService walletService;
    private final ContractEncryptionService encryptionService;
    private final ResourceLoader resourceLoader;

    @Value("classpath:fonts/NanumGothic.ttf")
    private Resource fontResource;

    @Value("classpath:fonts/NanumGothicBold.ttf")
    private Resource boldFontResource;

    @Value("classpath:images/logo.png")
    private Resource logoResource;

    // 색상 정의 (SVG에서 사용된 색상)
    private static final Color BACKGROUND_COLOR = ColorConstants.WHITE; // #ffffff
    private static final Color HEADER_COLOR = new DeviceRgb(250, 250, 250); // #fafafa
    private static final Color TEXT_COLOR = new DeviceRgb(51, 51, 51); // #333333
    private static final Color MUTED_TEXT_COLOR = new DeviceRgb(136, 136, 136); // #888888
    private static final Color BORDER_COLOR = new DeviceRgb(208, 208, 208); // #d0d0d0
    private static final Color ERROR_COLOR = new DeviceRgb(204, 0, 0); // #cc0000
    private static final Color PRIMARY_COLOR = new DeviceRgb(85, 85, 85); // #555555
    private static final Color TABLE_ODD_ROW = new DeviceRgb(245, 245, 245); // #f5f5f5
    private static final Color TABLE_EVEN_ROW = ColorConstants.WHITE; // #ffffff
    private static final Color LOGO_BG_COLOR = new DeviceRgb(51, 51, 51); // #333333

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

    /**
     * 폰트 로드
     */
    private PdfFont loadFont(Resource resource) throws IOException {
        try {
            byte[] fontBytes = resource.getInputStream().readAllBytes();
            return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            log.error("폰트 로드 실패: {}", e.getMessage());
            return PdfFontFactory.createFont();
        }
    }

    /**
     * 계약 상세 정보를 바탕으로 화이트 테마 차용증 PDF 생성
     * @param contract 계약 엔티티
     * @return PDF 파일 바이트 배열
     */
    public byte[] generateWhiteThemeContractPdf(Contract contract) {
        log.debug("화이트 테마 차용증 PDF 생성: 계약 ID = {}", contract.getContractId());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // 모든 페이지에 이벤트 핸들러 추가 (색상 통일)
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
                    new PdfEventHandler.AddBackgroundColor(BACKGROUND_COLOR));

            Document document = new Document(pdfDoc, PageSize.A4);

            // 기본 폰트 설정
            PdfFont font = loadFont(fontResource);
            PdfFont boldFont = loadFont(boldFontResource);
            document.setFont(font);
            document.setFontColor(TEXT_COLOR);

            // 문서 구성
            addHeader(document, "전자 차용증 (계약번호: " + contract.getContractId() + ")", boldFont);
            addDocumentTitle(document, "전자 차용증", boldFont);
            addContractParties(document, contract, boldFont);
            addLoanDetails(document, contract, boldFont);
            addContractTerms(document, contract, boldFont);
            addSignatureSection(document, contract, boldFont);
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    /**
     * 암호화된 화이트 테마 차용증 PDF 생성 (IPFS 업로드용)
     * @param contract 계약 엔티티
     * @return 암호화된 PDF 파일 바이트 배열
     */
    public byte[] generateEncryptedWhiteThemeContractPdf(Contract contract) {
        log.debug("암호화된 화이트 테마 차용증 PDF 생성: 계약 ID = {}", contract.getContractId());

        // 계약에 대한 새 암호화 키 생성 (아직 키가 없는 경우)
        if (contract.getEncryptedContractKey() == null) {
            encryptionService.generateAndStoreKeyForContract(contract);
            log.info("계약 ID {}에 대한 암호화 키 새로 생성됨", contract.getContractId());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // 모든 페이지에 이벤트 핸들러 추가 (색상 통일)
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
                    new PdfEventHandler.AddBackgroundColor(BACKGROUND_COLOR));

            Document document = new Document(pdfDoc, PageSize.A4);

            // 기본 폰트 설정
            PdfFont font = loadFont(fontResource);
            PdfFont boldFont = loadFont(boldFontResource);
            document.setFont(font);
            document.setFontColor(TEXT_COLOR);

            // 문서 구성 (암호화 적용)
            addHeader(document, "전자 차용증 (계약번호: " + contract.getContractId() + ")", boldFont);
            addDocumentTitle(document, "전자 차용증", boldFont);
            addEncryptedContractParties(document, contract, boldFont);
            addLoanDetails(document, contract, boldFont);
            addContractTerms(document, contract, boldFont);
            addEncryptedSignatureSection(document, contract, boldFont);
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("암호화된 PDF 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화된 PDF 생성 실패", e);
        }
    }

    /**
     * 헤더 추가
     */
    private void addHeader(Document document, String title, PdfFont boldFont) throws IOException {
        // 헤더 배경 (직사각형)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setBackgroundColor(HEADER_COLOR);
        headerTable.setBorder(new SolidBorder(BORDER_COLOR, 1));
        headerTable.setMargin(0);
        headerTable.setPadding(10);

        try {
            // 로고 이미지 사용 (실제 로고 이미지가 있는 경우)
            Image logo = new Image(ImageDataFactory.create(logoResource.getInputStream().readAllBytes()));
            logo.setHeight(50);

            Cell logoCell = new Cell();
            logoCell.add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(logoCell);
        } catch (IOException e) {
            // 로고 이미지가 없는 경우, 대체 텍스트 로고 사용
            log.warn("로고 이미지 로드 실패: {}", e.getMessage());

            // 로고 컨테이너 (검은 배경에 R 텍스트)
            Cell logoCell = new Cell();

            // 검은 배경의 R 로고
            Div logoContainer = new Div();
            logoContainer.setBackgroundColor(LOGO_BG_COLOR);
            logoContainer.setPadding(10);
            logoContainer.setWidth(50);
            logoContainer.setHeight(50);
            logoContainer.setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(5));

            Paragraph logoParagraph = new Paragraph("R")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);

            logoContainer.add(logoParagraph);
            logoCell.add(logoContainer);
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(logoCell);
        }

        // 제목 추가
        Cell titleCell = new Cell();
        titleCell.add(new Paragraph(title)
                .setFontSize(18)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT));
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        headerTable.addCell(titleCell);

        document.add(headerTable);

        // 구분선 추가
        document.add(new LineSeparator(new DashedLine(0.5f))
                .setMarginTop(10)
                .setMarginBottom(20)
                .setStrokeColor(BORDER_COLOR));
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
     * 계약 당사자 정보 섹션 추가
     */
    private void addContractParties(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("1. 계약 당사자")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        // 테이블을 고정 비율로 설정 (정확히 50%씩)
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
     * 암호화된 계약 당사자 정보 섹션 추가
     */
    private void addEncryptedContractParties(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("1. 계약 당사자")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        // 테이블을 고정 비율로 설정 (정확히 50%씩)
        Table partiesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 정보 (암호화 적용)
        Map<String, String> creditorInfo = new HashMap<>();
        creditorInfo.put("이름", encryptionService.encryptWithPrefix(contract, contract.getCreditor().getUserName()));
        creditorInfo.put("이메일", encryptionService.encryptWithPrefix(contract, contract.getCreditor().getEmail()));
        creditorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getCreditor()));

        // 채무자 정보 (암호화 적용)
        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("이름", encryptionService.encryptWithPrefix(contract, contract.getDebtor().getUserName()));
        debtorInfo.put("이메일", encryptionService.encryptWithPrefix(contract, contract.getDebtor().getEmail()));
        debtorInfo.put("지갑 주소", walletService.getUserPrimaryWalletAddress(contract.getDebtor()));

        addPersonInfoSection(partiesTable, "채권자 (대출해주는 사람)", creditorInfo, boldFont);
        addPersonInfoSection(partiesTable, "채무자 (대출받는 사람)", debtorInfo, boldFont);

        document.add(partiesTable);
        document.add(new Paragraph().setMarginBottom(15));
    }

    /**
     * 인물 정보 섹션 테이블 구성 (개행 문제 해결)
     */
    private void addPersonInfoSection(Table parentTable, String title, Map<String, String> info, PdfFont boldFont) {
        // 제목
        Paragraph titleParagraph = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);

        // 정보 테이블
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        int rowIndex = 0;
        for (Map.Entry<String, String> entry : info.entrySet()) {
            // 키 셀 (홀수 행은 회색 배경, 짝수 행은 흰색 배경)
            Cell keyCell = new Cell();
            keyCell.add(new Paragraph(entry.getKey()).setFont(boldFont));
            keyCell.setBackgroundColor(rowIndex % 2 == 0 ? TABLE_ODD_ROW : TABLE_EVEN_ROW);
            keyCell.setBorder(new SolidBorder(BORDER_COLOR, 1));
            infoTable.addCell(keyCell);

            // 값 셀 - 자동 줄바꿈을 위한 설정 강화
            Color bgColor = rowIndex % 2 == 0 ? TABLE_ODD_ROW : TABLE_EVEN_ROW;
            Cell valueCell = createValueCellWithWordWrap(entry.getValue(), bgColor, TEXT_COLOR);
            valueCell.setBorder(new SolidBorder(BORDER_COLOR, 1));
            infoTable.addCell(valueCell);

            rowIndex++;
        }

        Cell cell = new Cell();
        cell.add(titleParagraph);
        cell.add(new Paragraph().setMarginBottom(5));
        cell.add(infoTable);
        cell.setBorder(Border.NO_BORDER);

        parentTable.addCell(cell);
    }

    /**
     * 대출 정보 섹션 추가
     */
    private void addLoanDetails(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("2. 대출 정보")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .setWidth(UnitValue.createPercentValue(100));

        // 대출 정보 항목
        addTableRow(table, "대출 금액", formatAmount(contract.getLoanAmount()) + " 원", boldFont, 0);
        addTableRow(table, "연 이자율", contract.getInterestRate() + "%", boldFont, 1, PRIMARY_COLOR);
        addTableRow(table, "계약일", formatDate(contract.getContractDate()), boldFont, 2);
        addTableRow(table, "만기일", formatDate(contract.getMaturityDate()), boldFont, 3);
        addTableRow(table, "대출 기간", contract.getLoanTerm() + "개월", boldFont, 4);
        addTableRow(table, "상환 방식", contract.getRepaymentType().getCodeName(), boldFont, 5);
        addTableRow(table, "연체 이자율", contract.getDefaultInterestRate() + "%", boldFont, 6, ERROR_COLOR);

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    /**
     * 테이블 행 추가 (기본 텍스트 색상)
     */
    private void addTableRow(Table table, String key, String value, PdfFont boldFont, int rowIndex) {
        addTableRow(table, key, value, boldFont, rowIndex, TEXT_COLOR);
    }

    /**
     * 테이블 행 추가 (커스텀 텍스트 색상) - 개행 문제 해결
     */
    private void addTableRow(Table table, String key, String value, PdfFont boldFont, int rowIndex, Color valueColor) {
        // 키 셀
        Cell keyCell = new Cell();
        keyCell.add(new Paragraph(key).setFont(boldFont));
        keyCell.setBackgroundColor(rowIndex % 2 == 0 ? TABLE_ODD_ROW : TABLE_EVEN_ROW);
        keyCell.setBorder(new SolidBorder(BORDER_COLOR, 1));
        table.addCell(keyCell);

        // 값 셀 - 개선된 자동 줄바꿈 적용
        Color bgColor = rowIndex % 2 == 0 ? TABLE_ODD_ROW : TABLE_EVEN_ROW;
        Cell valueCell = createValueCellWithWordWrap(value, bgColor, valueColor);
        valueCell.setBorder(new SolidBorder(BORDER_COLOR, 1));
        table.addCell(valueCell);
    }

    /**
     * 계약 조항 섹션 추가
     */
    private void addContractTerms(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("3. 계약 조항")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        // 계약 조항 배경 컨테이너
        Div termsContainer = new Div();
        termsContainer.setBackgroundColor(HEADER_COLOR);
        termsContainer.setBorder(new SolidBorder(BORDER_COLOR, 1));
        termsContainer.setPadding(10);
        termsContainer.setMarginBottom(20);

        // 기본 계약 조항
        String[] defaultTerms = {
                "1. 채무자는 약정된 만기일에 원금과 이자를 합한 금액을 채권자에게 상환한다.",
                "2. 상환이 지연될 경우 지연된 날로부터 연체이자가 적용된다.",
                "3. 연속적인 상환 불이행 시 기한이익이 상실되어 잔여 대출금 전액을 즉시 상환해야 한다.",
                "4. 본 계약은 블록체인 상에 기록되며 NFT로 발행된다."
        };

        for (String term : defaultTerms) {
            Paragraph p = new Paragraph(term)
                    .setFontSize(11)
                    .setMarginBottom(5);
            // 긴 텍스트 자동 줄바꿈 설정
            p.setWidth(UnitValue.createPercentValue(100));
            p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
            termsContainer.add(p);
        }

        // 추가 계약 조항 (있는 경우)
        if (contract.getContractTerms() != null && !contract.getContractTerms().isEmpty()) {
            Paragraph additionalTermsTitle = new Paragraph("추가 계약 조항")
                    .setFont(boldFont)
                    .setMarginTop(10)
                    .setMarginBottom(5);
            termsContainer.add(additionalTermsTitle);

            // 줄바꿈으로 구분된 추가 조항
            String[] additionalTerms = contract.getContractTerms().split("\n");
            for (String term : additionalTerms) {
                // 개선된, 줄바꿈 처리된 단락 추가
                Cell termCell = createValueCellWithWordWrap(term, HEADER_COLOR, TEXT_COLOR);
                termCell.setBorder(Border.NO_BORDER);
                termCell.setFontSize(11);
                termCell.setMarginBottom(5);
                termsContainer.add(termCell);
            }
        }

        document.add(termsContainer);
    }

    /**
     * 서명란 섹션 추가 (개행 문제 해결)
     */
    private void addSignatureSection(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("4. 서명")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Paragraph signatureParagraph = new Paragraph(
                "본 전자 차용증은 블록체인 상의 스마트 계약으로 체결되었으며, " +
                        "각 당사자의 전자 서명(지갑 주소를 통한 트랜잭션)으로 효력이 발생합니다.")
                .setMarginBottom(15);
        // 긴 텍스트 자동 줄바꿈 설정
        signatureParagraph.setWidth(UnitValue.createPercentValue(100));
        signatureParagraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        document.add(signatureParagraph);

        // 서명 구분선
        SolidLine solidLine = new SolidLine(1);
        solidLine.setColor(BORDER_COLOR);
        document.add(new LineSeparator(solidLine)
                .setMarginBottom(15));

        // 서명 테이블 (정확히 50:50 비율로 설정)
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 서명 정보 (createValueCellWithWordWrap 사용)
        Cell creditorCell = new Cell();
        creditorCell.setBorder(Border.NO_BORDER);

        // 채권자 이름 (제목)
        Paragraph creditorTitle = new Paragraph("채권자:");
        creditorTitle.setFont(boldFont);
        creditorCell.add(creditorTitle);

        // 채권자 이름 (값) - 줄바꿈 적용
        String creditorName = contract.getCreditor().getNickname();
        Cell creditorNameCell = createValueCellWithWordWrap(creditorName, ColorConstants.WHITE, TEXT_COLOR);
        creditorNameCell.setBorder(Border.NO_BORDER);
        creditorCell.add(creditorNameCell);

        // 채권자 지갑 주소 (제목)
        Paragraph walletTitle = new Paragraph("지갑 주소:");
        creditorCell.add(walletTitle);

        // 채권자 지갑 주소 (값) - 줄바꿈 적용
        String creditorWallet = walletService.getUserPrimaryWalletAddress(contract.getCreditor());
        Cell walletCell = createValueCellWithWordWrap(creditorWallet, ColorConstants.WHITE, TEXT_COLOR);
        walletCell.setBorder(Border.NO_BORDER);
        creditorCell.add(walletCell);

        // 채권자 서명일시
        Paragraph dateParagraph = new Paragraph("서명일시: " + formatDateTime(contract.getUpdatedAt()));
        dateParagraph.setFontColor(MUTED_TEXT_COLOR);
        creditorCell.add(dateParagraph);

        signatureTable.addCell(creditorCell);

        // 채무자 서명 정보 (createValueCellWithWordWrap 사용)
        Cell debtorCell = new Cell();
        debtorCell.setBorder(Border.NO_BORDER);

        // 채무자 이름 (제목)
        Paragraph debtorTitle = new Paragraph("채무자:");
        debtorTitle.setFont(boldFont);
        debtorCell.add(debtorTitle);

        // 채무자 이름 (값) - 줄바꿈 적용
        String debtorName = contract.getDebtor().getNickname();
        Cell debtorNameCell = createValueCellWithWordWrap(debtorName, ColorConstants.WHITE, TEXT_COLOR);
        debtorNameCell.setBorder(Border.NO_BORDER);
        debtorCell.add(debtorNameCell);

        // 채무자 지갑 주소 (제목)
        Paragraph debtorWalletTitle = new Paragraph("지갑 주소:");
        debtorCell.add(debtorWalletTitle);

        // 채무자 지갑 주소 (값) - 줄바꿈 적용
        String debtorWallet = walletService.getUserPrimaryWalletAddress(contract.getDebtor());
        Cell debtorWalletCell = createValueCellWithWordWrap(debtorWallet, ColorConstants.WHITE, TEXT_COLOR);
        debtorWalletCell.setBorder(Border.NO_BORDER);
        debtorCell.add(debtorWalletCell);

        // 채무자 서명일시
        Paragraph debtorDateParagraph = new Paragraph("서명일시: " + formatDateTime(contract.getCreatedAt()));
        debtorDateParagraph.setFontColor(MUTED_TEXT_COLOR);
        debtorCell.add(debtorDateParagraph);

        signatureTable.addCell(debtorCell);
        document.add(signatureTable);

        // NFT 정보 추가 (토큰 ID가 있는 경우)
        if (contract.getTokenId() != null) {
            document.add(new Paragraph().setMarginBottom(15));

            Paragraph nftInfo = new Paragraph();
            Text nftTitle = new Text("NFT 정보:\n");
            nftTitle.setFont(boldFont);
            nftInfo.add(nftTitle);

            // 토큰 ID에도 줄바꿈 적용
            Cell tokenIdCell = createValueCellWithWordWrap("토큰 ID: " + contract.getTokenId(), ColorConstants.WHITE, TEXT_COLOR);
            tokenIdCell.setBorder(Border.NO_BORDER);
            document.add(nftInfo);
            document.add(tokenIdCell);
        }
    }

    /**
     * 암호화된 서명란 섹션 추가 (개행 문제 해결)
     */
    private void addEncryptedSignatureSection(Document document, Contract contract, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph("4. 서명")
                .setFontSize(16)
                .setFont(boldFont)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Paragraph signatureParagraph = new Paragraph(
                "본 전자 차용증은 블록체인 상의 스마트 계약으로 체결되었으며, " +
                        "각 당사자의 전자 서명(지갑 주소를 통한 트랜잭션)으로 효력이 발생합니다.")
                .setMarginBottom(15);
        // 긴 텍스트 자동 줄바꿈 설정
        signatureParagraph.setWidth(UnitValue.createPercentValue(100));
        signatureParagraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        document.add(signatureParagraph);

        // 서명 구분선
        SolidLine solidLine = new SolidLine(1);
        solidLine.setColor(BORDER_COLOR);
        document.add(new LineSeparator(solidLine)
                .setMarginBottom(15));

        // 서명 테이블 (정확히 50:50 비율로 설정)
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // 채권자 서명 정보 (암호화 적용, createValueCellWithWordWrap 사용)
        Cell creditorCell = new Cell();
        creditorCell.setBorder(Border.NO_BORDER);

        // 채권자 이름 (제목)
        Paragraph creditorTitle = new Paragraph("채권자:");
        creditorTitle.setFont(boldFont);
        creditorCell.add(creditorTitle);

        // 암호화된 채권자 이름 (값) - 줄바꿈 적용
        String encryptedCreditorName = encryptionService.encryptWithPrefix(contract, contract.getCreditor().getPassCode());
        Cell creditorNameCell = createValueCellWithWordWrap(encryptedCreditorName, ColorConstants.WHITE, TEXT_COLOR);
        creditorNameCell.setBorder(Border.NO_BORDER);
        creditorCell.add(creditorNameCell);

        // 채권자 지갑 주소 (제목)
        Paragraph walletTitle = new Paragraph("지갑 주소:");
        creditorCell.add(walletTitle);

        // 채권자 지갑 주소 (값) - 줄바꿈 적용
        String creditorWallet = walletService.getUserPrimaryWalletAddress(contract.getCreditor());
        Cell walletCell = createValueCellWithWordWrap(creditorWallet, ColorConstants.WHITE, TEXT_COLOR);
        walletCell.setBorder(Border.NO_BORDER);
        creditorCell.add(walletCell);

        // 채권자 서명일시
        Paragraph dateParagraph = new Paragraph("서명일시: " + formatDateTime(contract.getUpdatedAt()));
        dateParagraph.setFontColor(MUTED_TEXT_COLOR);
        creditorCell.add(dateParagraph);

        signatureTable.addCell(creditorCell);

        // 채무자 서명 정보 (암호화 적용, createValueCellWithWordWrap 사용)
        Cell debtorCell = new Cell();
        debtorCell.setBorder(Border.NO_BORDER);

        // 채무자 이름 (제목)
        Paragraph debtorTitle = new Paragraph("채무자:");
        debtorTitle.setFont(boldFont);
        debtorCell.add(debtorTitle);

        // 암호화된 채무자 이름 (값) - 줄바꿈 적용
        String encryptedDebtorName = encryptionService.encryptWithPrefix(contract, contract.getDebtor().getPassCode());
        Cell debtorNameCell = createValueCellWithWordWrap(encryptedDebtorName, ColorConstants.WHITE, TEXT_COLOR);
        debtorNameCell.setBorder(Border.NO_BORDER);
        debtorCell.add(debtorNameCell);

        // 채무자 지갑 주소 (제목)
        Paragraph debtorWalletTitle = new Paragraph("지갑 주소:");
        debtorCell.add(debtorWalletTitle);

        // 채무자 지갑 주소 (값) - 줄바꿈 적용
        String debtorWallet = walletService.getUserPrimaryWalletAddress(contract.getDebtor());
        Cell debtorWalletCell = createValueCellWithWordWrap(debtorWallet, ColorConstants.WHITE, TEXT_COLOR);
        debtorWalletCell.setBorder(Border.NO_BORDER);
        debtorCell.add(debtorWalletCell);

        // 채무자 서명일시
        Paragraph debtorDateParagraph = new Paragraph("서명일시: " + formatDateTime(contract.getCreatedAt()));
        debtorDateParagraph.setFontColor(MUTED_TEXT_COLOR);
        debtorCell.add(debtorDateParagraph);

        signatureTable.addCell(debtorCell);
        document.add(signatureTable);

        // NFT 정보 추가 (토큰 ID가 있는 경우)
        if (contract.getTokenId() != null) {
            document.add(new Paragraph().setMarginBottom(15));

            Paragraph nftInfo = new Paragraph();
            Text nftTitle = new Text("NFT 정보:\n");
            nftTitle.setFont(boldFont);
            nftInfo.add(nftTitle);

            // 토큰 ID에도 줄바꿈 적용
            Cell tokenIdCell = createValueCellWithWordWrap("토큰 ID: " + contract.getTokenId(), ColorConstants.WHITE, TEXT_COLOR);
            tokenIdCell.setBorder(Border.NO_BORDER);
            document.add(nftInfo);
            document.add(tokenIdCell);
        }
    }

    /**
     * 푸터 섹션 추가
     */
    private void addFooter(Document document) {
        // 구분선 추가
        document.add(new LineSeparator(new DashedLine(0.5f))
                .setMarginTop(15)
                .setMarginBottom(10)
                .setStrokeColor(BORDER_COLOR));

        // 푸터 테이블
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));

        // 생성 일시
        String currentDateTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Text dateText = new Text("생성일시: " + currentDateTime);
        dateText.setFontColor(MUTED_TEXT_COLOR);
        dateText.setFontSize(8);

        Paragraph dateParagraph = new Paragraph();
        dateParagraph.add(dateText);

        Cell dateCell = new Cell();
        dateCell.add(dateParagraph);
        dateCell.setBorder(Border.NO_BORDER);
        footerTable.addCell(dateCell);

        // 저작권 정보
        Text copyrightText = new Text("© Rabbit Finance. All rights reserved.");
        copyrightText.setFontColor(MUTED_TEXT_COLOR);
        copyrightText.setFontSize(8);

        Paragraph copyrightParagraph = new Paragraph();
        copyrightParagraph.add(copyrightText);
        copyrightParagraph.setTextAlignment(TextAlignment.RIGHT);

        Cell copyrightCell = new Cell();
        copyrightCell.add(copyrightParagraph);
        copyrightCell.setBorder(Border.NO_BORDER);
        footerTable.addCell(copyrightCell);

        document.add(footerTable);
    }

    /**
     * 값 셀의 줄바꿈을 강제하기 위한 헬퍼 메서드
     */
    private Cell createValueCellWithWordWrap(String value, Color backgroundColor, Color textColor) {
        Cell valueCell = new Cell();
        // 셀 자체에 줄바꿈 속성 지정
        valueCell.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        valueCell.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

        // 비어있는 경우 처리
        if (value == null || value.isEmpty()) {
            value = "";
        }

        // 긴 문자열을 강제로 적절한 길이에서 분할
        StringBuilder wrappedText = new StringBuilder();
        int maxCharsPerLine = 25; // 한 줄당 최대 글자수 (한글 기준)

        int currentLength = 0;
        String[] words = value.split(" ");

        for (String word : words) {
            // 단어가 너무 길면 (URL 등) 그 단어 자체를 분할
            if (word.length() > maxCharsPerLine) {
                // 현재까지의 내용을 먼저 추가
                if (currentLength > 0) {
                    wrappedText.append(" ");
                    currentLength++;
                }

                // 긴 단어 분할
                for (int i = 0; i < word.length(); i += maxCharsPerLine) {
                    int endIndex = Math.min(i + maxCharsPerLine, word.length());
                    wrappedText.append(word.substring(i, endIndex));
                    if (endIndex < word.length()) {
                        wrappedText.append("\n");
                        currentLength = 0;
                    } else {
                        currentLength = endIndex - i;
                    }
                }
            }
            // 일반적인 단어 처리
            else {
                if (currentLength + word.length() + 1 > maxCharsPerLine) {
                    // 새 줄 추가
                    wrappedText.append("\n").append(word);
                    currentLength = word.length();
                } else {
                    // 같은 줄에 추가
                    if (currentLength > 0) {
                        wrappedText.append(" ");
                        currentLength++;
                    }
                    wrappedText.append(word);
                    currentLength += word.length();
                }
            }
        }

        // 줄바꿈이 적용된 텍스트로 Paragraph 생성
        Paragraph valueParagraph = new Paragraph(wrappedText.toString());
        valueParagraph.setFontColor(textColor);

        // 추가적인 줄바꿈 속성 지정
        valueParagraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        valueParagraph.setWidth(UnitValue.createPercentValue(100));

        // 셀에 단락 추가
        valueCell.add(valueParagraph);
        valueCell.setBackgroundColor(backgroundColor);

        return valueCell;
    }

    // 유틸리티 메서드

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
        return dateTime.format(DATETIME_FORMATTER);
    }
}