package com.topy.bookreview.global.exception;


import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  USER_NOT_FOUND(SC_NOT_FOUND, "회원을 찾을 수 없습니다."),
  REVIEW_NOT_FOUND(SC_NOT_FOUND, "리뷰를 찾을 수 없습니다."),
  LIKE_NOT_FOUND(SC_NOT_FOUND, "'좋아요'가 존재하지 않습니다."),

  ALREADY_EMAIL_VERIFIED_USER(SC_CONFLICT, "이미 이메일 인증된 회원입니다."),
  ALREADY_EXISTS_EMAIL(SC_CONFLICT, "이미 존재하는 이메일입니다."),
  ALREADY_EXISTS_NICKNAME(SC_CONFLICT, "이미 존재하는 닉네임입니다."),
  ALREADY_EXIST_LIKE(SC_CONFLICT, "이미 해당 리뷰에 '좋아요'를 누른 회원입니다."),

  EMAIL_IS_NOT_VERIFIED(SC_UNAUTHORIZED, "이메일 인증이 되지 않았습니다."),

  EXPIRED_AUTH_CODE(SC_UNAUTHORIZED, "인증 코드가 만료되었습니다."),
  UNMATCHED_AUTH_CODE(SC_BAD_REQUEST, "인증 코드가 일치하지 않습니다."),

  EXPIRED_ACCESS_TOKEN(SC_UNAUTHORIZED, "만료된 액세스 토큰입니다. 리프래시 토큰을 보내주세요."),
  EXPIRED_REFRESH_TOKEN(SC_UNAUTHORIZED, "만료된 리프래시 토큰입니다. 다시 로그인 해주세요."),

  REFRESH_TOKEN_NOT_EXIST(SC_UNAUTHORIZED, "리프래시 토큰이 존재하지 않습니다."),
  INVALID_TOKEN(SC_BAD_REQUEST, "잘못된 토큰입니다."),
  COOKIE_NOT_FOUND(SC_NOT_FOUND, "존재하지 않는 쿠키입니다."),
  FORBIDDEN_ACCESS(SC_FORBIDDEN, "잘못된 접근입니다."),

  SEARCH_RESULT_EMPTY(SC_NOT_FOUND, "조회된 결과가 없습니다."),

  CACHE_DATA_CONVERT_ERROR(SC_INTERNAL_SERVER_ERROR, "캐시 데이터의 타입이 올바르지 않습니다."),
  NOTIFICATION_SEND_ERROR(SC_INTERNAL_SERVER_ERROR, "알림 전송 오류가 발생하였습니다."),
  SSE_EMITTER_NOT_FOUND(SC_NOT_FOUND, "SSE-Emitter를 찾을 수 없습니다."),
  ;

  private final int code;

  private final String message;
}
