package com.topy.bookreview.security.filter;

import static com.topy.bookreview.global.exception.ErrorCode.EXPIRED_REFRESH_TOKEN;

import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.manager.JwtManager;
import com.topy.bookreview.global.util.CookieUtils;
import com.topy.bookreview.redis.RedisManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final static String TOKEN_HEADER = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";
  private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  private final JwtManager jwtManager;
  private final RedisManager redisManager;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = resolveAccessToken(request);

    if (StringUtils.hasText(accessToken)) {
      if (jwtManager.isExpiredToken(accessToken)) {
        log.info("Expired accessToken = {}", accessToken);

        String refreshToken = CookieUtils.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

        if (jwtManager.isExpiredToken(refreshToken)) {
          log.info("Expired refreshToken = {}", refreshToken);
          redisManager.delete(refreshToken);
          throw new CustomException(EXPIRED_REFRESH_TOKEN);
        }

        log.info("Verifying refreshToken = {}", refreshToken);
        Authentication authentication = jwtManager.createAuthentication(refreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = jwtManager.generateAccessToken(authentication);

        response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + newAccessToken);

      } else {
        log.info("Verifying accessToken = {}", accessToken);
        Authentication authentication = jwtManager.createAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveAccessToken(HttpServletRequest request) {
    String accessToken = request.getHeader(TOKEN_HEADER);
    if (StringUtils.hasText(accessToken) && accessToken.startsWith(TOKEN_PREFIX)) {
      return accessToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}


