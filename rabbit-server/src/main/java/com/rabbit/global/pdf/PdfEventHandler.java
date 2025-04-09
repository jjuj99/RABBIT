package com.rabbit.global.pdf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * PDF 문서 이벤트 핸들러 클래스
 * - 배경색 지정 등 PDF 페이지 관련 이벤트 처리
 */
public class PdfEventHandler {

    /**
     * 페이지 배경색 추가 이벤트 핸들러
     */
    public static class AddBackgroundColor extends AbstractPdfDocumentEventHandler {
        private Color backgroundColor;

        public AddBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            // 페이지 크기 가져오기
            Rectangle pageSize = page.getPageSize();

            // 배경색 적용
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            canvas.saveState()
                    .setFillColor(backgroundColor)
                    .rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight())
                    .fill()
                    .restoreState();
            canvas.release();
        }
    }
}