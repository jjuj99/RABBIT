package com.rabbit.auction.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.rabbit.global.pdf.CommonPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuctionTransferPdfService {

    private final CommonPdfService pdfService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(Locale.KOREA);

    public byte[] generateTransferAgreementPdf(
            String assignorName, String assignorAddress,
            String assigneeName, String assigneeAddress,
            String debtorName, String debtorAddress,
            BigDecimal transferredAmount,
            LocalDate contractDate
    ) {
        String title = "채권양도양수 계약서";

        return pdfService.generatePdf(title, (document, font, boldFont) -> {
            addTitle(document, title, boldFont);
            addPartyInfo(document, assignorName, assignorAddress, assigneeName, assigneeAddress, debtorName, debtorAddress, boldFont);
            addAgreementTerms(document, transferredAmount, boldFont);
            addAttachmentInfo(document, contractDate, assignorName, assigneeName, boldFont);
        });
    }

    private void addTitle(Document document, String title, PdfFont boldFont) {
        Paragraph paragraph = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(paragraph);
    }

    private void addPartyInfo(Document document, String assignor, String assignorAddr, String assignee, String assigneeAddr,
                              String debtor, String debtorAddr, PdfFont boldFont) {
        Paragraph assignorP = new Paragraph("양도인: " + assignor + "\n" + "지갑 주소: " + assignorAddr);
        Paragraph assigneeP = new Paragraph("양수인: " + assignee + "\n" + "지갑 주소: " + assigneeAddr);
        Paragraph debtorP = new Paragraph("제3채무자: " + debtor + "\n" + "지갑 주소: " + debtorAddr);

        document.add(assignorP);
        document.add(new Paragraph("\n"));
        document.add(assigneeP);
        document.add(new Paragraph("\n"));
        document.add(debtorP);
        document.add(new Paragraph("\n"));
    }

    private void addAgreementTerms(Document document, BigDecimal amount, PdfFont boldFont) {
        String amountStr = CURRENCY_FORMAT.format(amount) + "원";

        Paragraph p1 = new Paragraph("1. 양도인은 양수인이 제3채무자에 대하여 가지는 대여금 채권 금 " + amountStr + "을 양수인에게 양도한다.");
        Paragraph p2 = new Paragraph("2. 양도인은 본 계약 체결 후 지체 없이 제3채무자에게 확정된 바 있는 증서로써 통지를 한다.");
        Paragraph p3 = new Paragraph("3. 양수인이 제3채무자로부터 양수금을 지급받는 경우 양도인의 양수인에 대한 대여금채무는 대등액으로 상계한다.");

        document.add(p1);
        document.add(new Paragraph("\n"));
        document.add(p2);
        document.add(new Paragraph("\n"));
        document.add(p3);
        document.add(new Paragraph("\n"));
    }

    private void addAttachmentInfo(Document document, LocalDate contractDate, String assignor, String assignee, PdfFont boldFont) {
//        document.add(new Paragraph("첨부 서류\n\n차용증 사본 1부\n\n"));

        String formattedDate = contractDate.format(DATE_FORMAT);
        document.add(new Paragraph(formattedDate + "\n\n"));

        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).setWidth(UnitValue.createPercentValue(100));

        Paragraph left = new Paragraph()
                .add(new Text("양도인 (채무자): ").setFont(boldFont))
                .add(assignor);
        Paragraph right = new Paragraph()
                .add(new Text("양수인 (채권자): ").setFont(boldFont))
                .add(assignee);

        signatureTable.addCell(left);
        signatureTable.addCell(right);

        document.add(signatureTable);
    }
}
