package com.topy.bookreview.security.filter;

import static com.topy.bookreview.global.exception.ErrorCode.EXPIRED_ACCESS_TOKEN;

import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.util.JwtUtils;
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

  private final JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = request.getHeader(TOKEN_HEADER);

    if (StringUtils.hasText(accessToken) && accessToken.startsWith(TOKEN_PREFIX)) {
      accessToken = accessToken.substring(TOKEN_PREFIX.length());

      if (jwtUtils.isExpiredToken(accessToken)) {
        throw new CustomException(EXPIRED_ACCESS_TOKEN);
      } else {
        Authentication authentication = jwtUtils.createAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }

}


