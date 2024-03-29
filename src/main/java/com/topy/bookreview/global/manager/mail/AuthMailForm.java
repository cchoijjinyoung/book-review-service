package com.topy.bookreview.global.manager.mail;

public class AuthMailForm extends EmailForm {

  private static final String SUBJECT = "[책 리뷰 서비스] 이메일 인증 확인 메일입니다.";

  private static final String LINK_URL = "/auth/mail/verify";

  public AuthMailForm(String recipient, String authCode, String host) {
    super(recipient, SUBJECT, createText(recipient, authCode, host), host);
  }

  private static String createText(String recipient, String authCode, String host) {
    return  "<h1>[이메일 인증]</h1>"
        + "<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>"
        + "<a href='http://" + host + ":8080" + LINK_URL + "?email="
        + recipient
        + "&authCode="
        + authCode
        + "' target='_blank'>이메일 인증 확인</a>";
  }
}
