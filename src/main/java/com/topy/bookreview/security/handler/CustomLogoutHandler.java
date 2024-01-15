package com.topy.bookreview.security.handler;

import static io.netty.util.CharsetUtil.UTF_8;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.topy.bookreview.global.util.CookieUtils;
import com.topy.bookreview.redis.repository.RefreshTokenRedisRepository;
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

  private final RefreshTokenRedisRepository refreshTokenRedisRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    log.info("로그아웃 중입니다...");

    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

    String refreshToken = CookieUtils.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

    // 이미 삭제된 데이터에 대해 삭제 명령을 하더라도, 에러가 발생하지 않고, true를 리턴한다.
    if (refreshTokenRedisRepository.delete(refreshToken)) {
      response.setStatus(SC_OK);
      response.setCharacterEncoding(UTF_8.name());
      response.setContentType(APPLICATION_JSON_VALUE);
    }
  }
}
