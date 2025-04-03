package com.rabbit.mail.service;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * 일반 텍스트 메일 전송 (템플릿 기반)
     */
    public void sendMail(String to, SysCommonCodes.MailTemplateType template, Object... args) {
        String subject = template.getSubject();
        String content = template.buildBody(args);
        sendSimpleTextMail(to, subject, content);
    }

    /**
     * 내부 전용: 텍스트 메일 발송
     */
    private void sendSimpleTextMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("your_email@gmail.com"); // ← 발신자 이메일 설정

            mailSender.send(message);
        } catch (Exception e) {
            log.error("메일 전송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * (선택) HTML 메일 필요 시 확장
     */
    public void sendHtmlMail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        helper.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }
}

