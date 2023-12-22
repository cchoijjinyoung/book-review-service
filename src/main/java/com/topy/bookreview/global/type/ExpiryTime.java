package com.topy.bookreview.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExpiryTime {

  ACCESS_TOKEN(1000L * 60 * 60),
  REFRESH_TOKEN(1000L * 60 * 60 * 24 * 14),
  AUTH_EMAIL_VERIFICATION(1000L * 60 * 60 * 24 * 3),
  ;

  private final long expiryTimeMillis;
}
