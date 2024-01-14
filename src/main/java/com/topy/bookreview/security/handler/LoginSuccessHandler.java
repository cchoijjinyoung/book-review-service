package com.topy.bookreview.security.handler;

import static com.topy.bookreview.redis.Topic.CHANNEL_NOTIFICATION;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import com.topy.bookreview.global.manager.JwtManager;
import com.topy.bookreview.redis.listener.NotificationSubscriber;
import com.topy.bookreview.redis.repository.RefreshTokenRedisRepository;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final static String TOKEN_HEADER = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";
  private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  private final JwtManager jwtManager;

  private final RefreshTokenRedisRepository refreshTokenRedisRepository;

  private final RedisMessageListenerContainer messageListenerContainer;

  private final NotificationSubscriber notificationSubscriber;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

    log.info("로그인에 성공하였습니다. user={}", principal.getUsername());

    String accessToken = jwtManager.generateAccessToken(authentication);
    String refreshToken = jwtManager.generateRefreshToken(authentication);

    refreshTokenRedisRepository.save(refreshToken);

    String channelName = CHANNEL_NOTIFICATION.getPrefix() + principal.getId();
    messageListenerContainer.addMessageListener(notificationSubscriber,
        new ChannelTopic(channelName));
    log.info("redis pub/sub 알림 채널 구독 ={}", channelName);

    Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");

    response.setStatus(SC_OK);
    response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);
    response.addCookie(refreshTokenCookie);
  }
}
