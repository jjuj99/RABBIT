package com.rabbit.mail.service;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * 기존 MailService를 확장한 이메일 서비스
 * PDF 첨부 기능을 추가
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExtendedMailService {

    private final JavaMailSender mailSender;
    private final MailService mailService;

    /**
     * HTML 형식의 이메일에 PDF 파일 첨부하여 발송
     * @param to 수신자 이메일
     * @param subject 이메일 제목
     * @param htmlContent HTML 형식의 이메일 내용
     * @param pdfContent PDF 파일 바이트 배열
     * @param pdfFileName PDF 파일명
     */
    public void sendHtmlMailWithPdf(String to, String subject, String htmlContent,
                                    byte[] pdfContent, String pdfFileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // multipart/mixed 타입의 메시지 생성
            MimeMultipart mixedMultipart = new MimeMultipart("mixed");

            // HTML 컨텐츠 파트
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            mixedMultipart.addBodyPart(htmlPart);

            // PDF 첨부 파일 파트
            MimeBodyPart pdfPart = new MimeBodyPart();
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfContent, "application/pdf");
            pdfPart.setDataHandler(new DataHandler(dataSource));
            pdfPart.setFileName(pdfFileName);
            mixedMultipart.addBodyPart(pdfPart);

            // 메시지에 멀티파트 설정
            message.setContent(mixedMultipart);

            // 메시지 헤더 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("your_email@gmail.com"); // 발신자 이메일 설정

            // 메일 발송
            mailSender.send(message);
            log.info("PDF 첨부 이메일 발송 성공: 수신자={}, 제목={}, PDF 파일명={}", to, subject, pdfFileName);

        } catch (MessagingException e) {
            log.error("PDF 첨부 이메일 발송 실패: {}", e.getMessage(), e);
            // 실패 시 일반 텍스트 메일로 대체 발송 시도
            try {
                mailService.sendMail(to, SysCommonCodes.MailTemplateType.CONTRACT_FALLBACK, subject);
            } catch (Exception fallbackException) {
                log.error("대체 이메일 발송도 실패: {}", fallbackException.getMessage());
            }
        }
    }

    /**
     * 계약 체결 알림 이메일 (PDF 첨부)
     * @param to 수신자 이메일
     * @param contractId 계약 ID
     * @param partyName 계약 상대방 이름
     * @param pdfContent PDF 파일 바이트 배열
     */
    public void sendContractCompletedEmail(String to, Integer contractId, String partyName, byte[] pdfContent) {
        String subject = "계약 체결 완료 알림 [계약 #" + contractId + "]";
        String fileName = "contract_" + contractId + ".pdf";

        String htmlContent = "<html><body>" +
                "<h2>계약 체결이 완료되었습니다.</h2>" +
                "<p>" + partyName + "님과의 계약(#" + contractId + ")이 성공적으로 체결되었습니다.</p>" +
                "<p>첨부된 PDF 파일에서 계약 내용을 확인하실 수 있습니다.</p>" +
                "<p>또한 웹사이트에서도 계약 내용을 확인하실 수 있습니다.</p>" +
                "<p>추가 문의사항이 있으시면 고객센터로 연락해 주세요.</p>" +
                "<br>" +
                "<p>감사합니다.<br>Rabbit Finance 팀</p>" +
                "</body></html>";

        sendHtmlMailWithPdf(to, subject, htmlContent, pdfContent, fileName);
    }

    /**
     * 계약 수정 요청 알림 이메일 (PDF 첨부)
     * @param to 수신자 이메일
     * @param contractId 계약 ID
     * @param partyName 계약 상대방 이름
     * @param rejectMessage 수정 요청 메시지
     * @param pdfContent PDF 파일 바이트 배열
     */
    public void sendContractModificationRequestEmail(String to, Integer contractId, String partyName,
                                                     String rejectMessage, byte[] pdfContent) {
        String subject = "계약 수정 요청 알림 [계약 #" + contractId + "]";
        String fileName = "contract_" + contractId + "_modification.pdf";

        String htmlContent = "<html><body>" +
                "<h2>계약 수정 요청이 접수되었습니다.</h2>" +
                "<p>" + partyName + "님이 계약(#" + contractId + ")의 수정을 요청하였습니다.</p>" +
                "<p><strong>요청 사유:</strong> " + (rejectMessage != null ? rejectMessage : "사유가 입력되지 않았습니다.") + "</p>" +
                "<p>첨부된 PDF 파일에서 현재 계약 내용을 확인하실 수 있습니다.</p>" +
                "<p>웹사이트에서 계약 내용을 수정하여 다시 요청해 주세요.</p>" +
                "<br>" +
                "<p>감사합니다.<br>Rabbit Finance 팀</p>" +
                "</body></html>";

        sendHtmlMailWithPdf(to, subject, htmlContent, pdfContent, fileName);
    }

    /**
     * 계약 취소 알림 이메일
     * @param to 수신자 이메일
     * @param contractId 계약 ID
     * @param partyName 계약 상대방 이름
     */
    public void sendContractCanceledEmail(String to, Integer contractId, String partyName) {
        String subject = "계약 취소 알림 [계약 #" + contractId + "]";

        // 이 경우는 PDF를 첨부하지 않고 일반 템플릿 메일 사용
        mailService.sendMail(to, SysCommonCodes.MailTemplateType.CONTRACT_CANCELED,
                contractId.toString(), partyName);
    }

    /**
     * 채무자에게 채권 양도 통지서 및 계약서 이메일 발송 (PDF 첨부)
     *
     * @param to           채무자 이메일
     * @param debtorName   채무자 이름
     * @param creditorName 새 채권자 이름 (양수인)
     * @param noticePdfContent    채권 양도 통지서 PDF (바이트 배열)
     * @param agreementPdfContent 채권 양도양수 계약서 PDF (바이트 배열)
     * @param auctionId    경매 ID (예: 경매 번호 또는 Token ID)
     */

    public void sendAuctionTransferNoticeEmail(String to, String debtorName, String creditorName,
                                               byte[] noticePdfContent, byte[] agreementPdfContent,
                                               Integer auctionId) {
        String subject = "채권 양도 통지서 및 계약서 안내 [경매 #" + auctionId + "]";
        String noticeFileName = "채권양도통지서_" + auctionId + ".pdf";
        String agreementFileName = "양도양수계약서_" + auctionId + ".pdf";

        String htmlContent = "<html><body>" +
                "<h2>채권 양도 관련 안내</h2>" +
                "<p>안녕하세요, " + debtorName + "님.</p>" +
                "<p>귀하의 채권에 대한 권리가 <strong>" + creditorName + "</strong>님에게 양도되었음을 알려드립니다.</p>" +
                "<p>첨부된 <strong>채권 양도 통지서</strong> 및 <strong>양도양수 계약서</strong>를 확인해 주세요.</p>" +
                "<br>" +
                "<p>본 메일은 정보 제공을 위한 안내이며, 별도의 회신이 필요하지 않습니다.</p>" +
                "<p>문의사항은 고객센터로 연락 부탁드립니다.</p>" +
                "<br>" +
                "<p>감사합니다.<br>Rabbit Finance 팀</p>" +
                "</body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMultipart multipart = new MimeMultipart("mixed");

            // HTML 본문 파트
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // 첫 번째 PDF (통지서)
            MimeBodyPart noticePart = new MimeBodyPart();
            noticePart.setDataHandler(new DataHandler(new ByteArrayDataSource(noticePdfContent, "application/pdf")));
            noticePart.setFileName(noticeFileName);
            multipart.addBodyPart(noticePart);

            // 두 번째 PDF (계약서)
            MimeBodyPart agreementPart = new MimeBodyPart();
            agreementPart.setDataHandler(new DataHandler(new ByteArrayDataSource(agreementPdfContent, "application/pdf")));
            agreementPart.setFileName(agreementFileName);
            multipart.addBodyPart(agreementPart);

            // 메시지 설정
            message.setContent(multipart);
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("your_email@gmail.com"); // 발신자 이메일

            mailSender.send(message);
            log.info("채권 양도 통지서 및 계약서 이메일 발송 완료: to={}, auctionId={}", to, auctionId);

        } catch (MessagingException e) {
            log.error("PDF 첨부 이메일 발송 실패: {}", e.getMessage(), e);
            try {
                mailService.sendMail(to, SysCommonCodes.MailTemplateType.CONTRACT_FALLBACK, subject);
            } catch (Exception fallbackException) {
                log.error("대체 이메일 발송도 실패: {}", fallbackException.getMessage());
            }
        }
    }


    public void sendAuctionContractToAssignor(String to, String userName, BigInteger tokenId, byte[] pdfContent) {
        String subject = "차용증 NFT 양도 계약 체결 완료 안내 [토큰 ID: " + tokenId + "]";
        String fileName = "양도양수계약서_" + tokenId + ".pdf";

        String htmlContent = "<html><body>" +
                "<h2>차용증 NFT 양도 계약이 체결되었습니다.</h2>" +
                "<p>안녕하세요, " + userName + "님.</p>" +
                "<p>귀하께서 보유하신 차용증 NFT(토큰 ID: " + tokenId + ")에 대한 양도 계약이 성공적으로 체결되었습니다.</p>" +
                "<p>첨부된 PDF 문서를 통해 계약 내용을 확인하실 수 있습니다.</p>" +
                "<p>해당 차용증 NFT는 낙찰자에게 정상적으로 이전 처리됩니다.</p>" +
                "<br>" +
                "<p>감사합니다.<br>Rabbit Finance 팀</p>" +
                "</body></html>";

        sendHtmlMailWithPdf(to, subject, htmlContent, pdfContent, fileName);
    }

    public void sendAuctionContractToAssignee(String to, String userName, BigInteger tokenId, byte[] pdfContent) {
        String subject = "차용증 NFT 양수 계약 체결 안내 [토큰 ID: " + tokenId + "]";
        String fileName = "양도양수계약서_" + tokenId + ".pdf";

        String htmlContent = "<html><body>" +
                "<h2>차용증 NFT 낙찰 및 양수 계약이 완료되었습니다.</h2>" +
                "<p>안녕하세요, " + userName + "님.</p>" +
                "<p>귀하께서 입찰하신 차용증 NFT(토큰 ID: " + tokenId + ")에 대한 양수 계약이 체결되었습니다.</p>" +
                "<p>첨부된 PDF 문서를 통해 계약 내용을 확인하실 수 있습니다.</p>" +
                "<p>계약이 완료됨에 따라 해당 차용증 NFT는 귀하의 지갑으로 이전되었습니다.</p>" +
                "<br>" +
                "<p>감사합니다.<br>Rabbit Finance 팀</p>" +
                "</body></html>";

        sendHtmlMailWithPdf(to, subject, htmlContent, pdfContent, fileName);
    }

}