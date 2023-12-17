package com.topy.bookreview.config.security.handler;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    log.error("아이디 혹은 비밀번호가 올바르지 않습니다.");

    response.setStatus(SC_BAD_REQUEST);

    // TODO: ErrorResponse 응답
    response.getWriter().print("400");
  }
}
