package com.topy.bookreview.global.util.mail;

import lombok.Getter;

@Getter
public abstract class EmailForm {

  private final String recipient;

  private final String subject;

  private final String text;

  public EmailForm(String recipient, String subject, String text) {
    this.recipient = recipient;
    this.subject = subject;
    this.text = text;
  }
}
