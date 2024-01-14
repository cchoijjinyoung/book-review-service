package com.topy.bookreview.global.manager.mail;

import lombok.Getter;

@Getter
public abstract class EmailForm {

  private final String recipient;

  private final String subject;

  private final String text;

  private final String host;

  public EmailForm(String recipient, String subject, String text, String host) {
    this.recipient = recipient;
    this.subject = subject;
    this.text = text;
    this.host = host;
  }
}
