package com.rabbit.global.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * PDF 문서 생성을 위한 공통 유틸리티 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonPdfService {

    private final ResourceLoader resourceLoader;

    @Value("${pdf.font.path:classpath:fonts/NanumGothic.ttf}")
    private String fontPath;

    @Value("${pdf.font.bold.path:classpath:fonts/NanumGothicBold.ttf}")
    private String boldFontPath;

    @Value("${pdf.logo.path:classpath:images/logo.png}")
    private String logoPath;

    /**
     * PDF 문서에 사용할 기본 폰트 로드
     * @return PdfFont 객체
     * @throws IOException 폰트 로드 실패 시 발생
     */
    protected PdfFont loadDefaultFont() throws IOException {
        try {
            byte[] fontBytes = resourceLoader.getResource(fontPath).getInputStream().readAllBytes();
            return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            log.error("기본 폰트 로드 실패: {}", e.getMessage());
            // 폰트 로드 실패 시 기본 Helvetica 폰트 사용
            return PdfFontFactory.createFont();
        }
    }

    /**
     * PDF 문서에 사용할 굵은 폰트 로드
     * @return PdfFont 객체
     * @throws IOException 폰트 로드 실패 시 발생
     */
    protected PdfFont loadBoldFont() throws IOException {
        try {
            byte[] fontBytes = resourceLoader.getResource(boldFontPath).getInputStream().readAllBytes();
            return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            log.error("굵은 폰트 로드 실패: {}", e.getMessage());
            // 굵은 폰트 로드 실패 시 기본 폰트 사용
            return loadDefaultFont();
        }
    }

    /**
     * PDF 문서 생성 및 반환
     * @param title 문서 제목
     * @param content 문서 내용 생성을 위한 콜백 인터페이스
     * @return PDF 바이트 배열
     */
    public byte[] generatePdf(String title, PdfContentGenerator content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document document = new Document(pdfDoc, PageSize.A4);

            // 기본 폰트 설정
            PdfFont font = loadDefaultFont();
            PdfFont boldFont = loadBoldFont();
            document.setFont(font);

            // 헤더 추가
            addHeader(document, title, boldFont);

            // 콜백을 통해 컨텐츠 추가
            content.generate(document, font, boldFont);

            // 푸터 추가
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    /**
     * PDF 헤더 섹션 추가
     * @param document PDF 문서
     * @param title 문서 제목
     * @param boldFont 굵은 폰트
     */
    private void addHeader(Document document, String title, PdfFont boldFont) throws IOException {
        // 로고와 제목이 있는 테이블 생성
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        try {
            // 로고 이미지 추가
            Image logo = new Image(ImageDataFactory.create(
                    resourceLoader.getResource(logoPath).getInputStream().readAllBytes()));
            logo.setHeight(50);

            Cell logoCell = new Cell().add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(logoCell);
        } catch (IOException e) {
            log.warn("로고 이미지 로드 실패: {}", e.getMessage());
            Cell logoCell = new Cell().add(new Paragraph("Rabbit Finance").setFont(boldFont));
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
                .setMarginTop(5)
                .setMarginBottom(15)
                .setStrokeColor(ColorConstants.GRAY));
    }

    /**
     * PDF 푸터 섹션 추가
     * @param document PDF 문서
     */
    private void addFooter(Document document) {
        // 현재 날짜와 페이지 번호가 있는 푸터 생성
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));

        // 날짜 추가
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Cell dateCell = new Cell();
        dateCell.add(new Paragraph("생성일시: " + currentDate)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.LEFT));
        dateCell.setBorder(Border.NO_BORDER);
        footerTable.addCell(dateCell);

        // 저작권 정보 추가
        Cell copyrightCell = new Cell();
        copyrightCell.add(new Paragraph("© Rabbit Finance. All rights reserved.")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT));
        copyrightCell.setBorder(Border.NO_BORDER);
        footerTable.addCell(copyrightCell);

        // 구분선 추가
        document.add(new LineSeparator(new DashedLine(0.5f))
                .setMarginTop(15)
                .setMarginBottom(5)
                .setStrokeColor(ColorConstants.GRAY));

        document.add(footerTable);
    }

    /**
     * 테이블 형식의 키-값 데이터 추가 유틸리티 메서드
     * @param document PDF 문서
     * @param dataMap 데이터 맵
     * @param boldFont 굵은 폰트
     */
    public void addKeyValueTable(Document document, Map<String, String> dataMap, PdfFont boldFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        dataMap.forEach((key, value) -> {
            // 키 셀
            Cell keyCell = new Cell();
            keyCell.add(new Paragraph(key)
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.RIGHT));
            keyCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addCell(keyCell);

            // 값 셀
            Cell valueCell = new Cell();
            valueCell.add(new Paragraph(value));
            table.addCell(valueCell);
        });

        document.add(table);
        document.add(new Paragraph().setMarginBottom(10));
    }

    /**
     * 내용을 PDF 파일로 저장
     * @param filePath 저장할 파일 경로
     * @param pdfContent PDF 바이트 배열
     * @throws IOException 파일 저장 실패 시 발생
     */
    public void savePdfToFile(String filePath, byte[] pdfContent) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(pdfContent);
        } catch (IOException e) {
            log.error("PDF 파일 저장 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * PDF 문서 내용 생성을 위한 함수형 인터페이스
     */
    @FunctionalInterface
    public interface PdfContentGenerator {
        void generate(Document document, PdfFont font, PdfFont boldFont) throws IOException;
    }
}