package com.topy.bookreview.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthMailService {

  private final JavaMailSender mailSender;

  private final static String LINK_URL = "/auth/verify";

  @Value("${spring.mail.username}")
  private String from;

  public void sendToEmail(String authCode, String email) {
    log.info("send to email: {}", email);
    mailSender.send(createEmailForm(authCode, email));
  }

  private MimeMessage createEmailForm(String authCode, String email) {
    log.info("create to email: {}", email);
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

    try {
      messageHelper.setSubject("[이메일 인증 요청]");
      messageHelper.setTo(email);
      messageHelper.setFrom(from);
      messageHelper.setText(createTextForm(email, authCode), true);
    } catch (MessagingException e) {
      throw new RuntimeException("이메일폼 생성 오류 발생");
    }
    return message;
  }

  private String createTextForm(String email, String authCode) {
    return "<h1>[이메일 인증]</h1>"
        + "<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>"
        + "<a href='http://localhost:8080" + LINK_URL + "?email="
        + email
        + "&authCode="
        + authCode
        + "' target='_blenk'>이메일 인증 확인</a>";
  }
}

