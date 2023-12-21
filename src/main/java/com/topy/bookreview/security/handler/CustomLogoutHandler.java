package com.topy.bookreview.security.handler;

import com.topy.bookreview.global.util.CookieUtils;
import com.topy.bookreview.redis.RedisManager;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  private final RedisManager redisManager;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    log.info("CustomLogoutHandler");

    String refreshToken = CookieUtils.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    redisManager.delete(refreshToken);
  }
}
