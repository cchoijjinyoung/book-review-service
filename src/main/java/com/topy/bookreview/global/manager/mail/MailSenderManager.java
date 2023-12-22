package com.topy.bookreview.global.manager.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailSenderManager {

  private final JavaMailSender mailSender;

  public void sendMail(EmailForm email) {
    MimeMessagePreparator msg = mimeMessage -> {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      mimeMessageHelper.setTo(email.getRecipient());
      mimeMessageHelper.setSubject(email.getSubject());
      mimeMessageHelper.setText(email.getText(), true);
    };

    try {
      mailSender.send(msg);

    } catch (Exception e) {
      log.error("메세지 전송 오류: ={}", e.getMessage());
    }
  }
}

