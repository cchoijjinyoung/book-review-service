package com.topy.bookreview.security.handler;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.global.util.JwtUtils;
import com.topy.bookreview.redis.RedisUtils;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtils jwtUtils;

  private final RedisUtils redisUtils;

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    log.info("[인증성공] user={}", customUserDetails.getEmail());
    log.info("authentication.getName()={}", authentication.getName());


    String accessToken = jwtUtils.generateAccessToken(authentication);
    String refreshToken = jwtUtils.generateRefreshToken(authentication);


    redisUtils.save(refreshToken, refreshToken);
    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");

    String accessTokenJson = objectMapper.writeValueAsString(accessToken);

    response.setStatus(SC_OK);
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(UTF_8.name());
    response.addCookie(refreshTokenCookie);
    objectMapper.writeValue(response.getWriter(), accessTokenJson);
  }
}
