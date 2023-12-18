package com.topy.bookreview.exception;


import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  USER_NOT_FOUND(SC_NOT_FOUND, "회원을 찾을 수 없습니다."),
  ALREADY_VERIFIED_USER(SC_CONFLICT, "이미 인증된 회원입니다."),

  ALREADY_EXISTS_EMAIL(SC_CONFLICT, "이미 존재하는 이메일입니다."),

  EXPIRED_VERIFICATION_CODE(SC_UNAUTHORIZED, "인증 코드가 만료되었습니다."),
  UNMATCHED_VERIFICATION_CODE(SC_BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
  ;

  private final int code;

  private final String message;
}
