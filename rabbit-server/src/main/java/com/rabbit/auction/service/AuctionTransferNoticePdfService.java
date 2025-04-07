package com.rabbit.auction.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
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
public class AuctionTransferNoticePdfService {

    private final CommonPdfService pdfService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(Locale.KOREA);

    public byte[] generateTransferNoticePdf(
            String assignorName, String assignorWallet,
            String assigneeName, String assigneeWallet,
            String debtorName, String debtorWallet,
            BigDecimal transferredAmount,
            LocalDate noticeDate
    ) {
        String title = "채권양도 통지서";

        return pdfService.generatePdf(title, (document, font, boldFont) -> {
            addTitle(document, title, boldFont);
            addIntro(document, debtorName, debtorWallet);
            addBody(document, assignorName, assignorWallet, assigneeName, assigneeWallet, transferredAmount);
            addFooter(document, noticeDate, assignorName, assignorWallet, boldFont);
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

    private void addIntro(Document document, String debtorName, String debtorWallet) {
        document.add(new Paragraph("수신: " + debtorName));
        document.add(new Paragraph("메타마스크 주소: " + debtorWallet));
        document.add(new Paragraph("\n"));
    }

    private void addBody(Document document,
                         String assignorName, String assignorWallet,
                         String assigneeName, String assigneeWallet,
                         BigDecimal amount) {
        String amountStr = CURRENCY_FORMAT.format(amount) + "원";

        document.add(new Paragraph("귀하에게 다음과 같이 채권 양도 사실을 통지합니다.\n"));

        document.add(new Paragraph(
                "본인은 귀하에 대하여 가지는 금전채권 중 일부인 " + amountStr + "을 " +
                        "다음의 양수인에게 양도하였습니다.\n\n"
        ));

        document.add(new Paragraph("양수인: " + assigneeName));
        document.add(new Paragraph("지갑 주소: " + assigneeWallet + "\n"));

        document.add(new Paragraph("양도인: " + assignorName));
        document.add(new Paragraph("지갑 주소: " + assignorWallet + "\n"));

        document.add(new Paragraph(
                "이에 따라 귀하의 채무 이행은 양수인에게 직접 하여야 하며, 본 통지서로써 「민법」 제450조에 따른 확정된 통지의 효력이 발생함을 알려드립니다."
        ));
    }

    private void addFooter(Document document, LocalDate noticeDate, String assignorName, String assignorWallet, PdfFont boldFont) {
        document.add(new Paragraph("\n" + noticeDate.format(DATE_FORMAT)));

        document.add(new Paragraph("\n양도인: ")
                .add(new Text(assignorName).setFont(boldFont)));

        document.add(new Paragraph("지갑 주소: " + assignorWallet));
    }
}
