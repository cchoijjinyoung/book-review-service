package com.topy.bookreview.client.kakao;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoSearchClientConfig {

  @Value("${kakao.rest-api-key}")
  private String authorization;

  private final static String AUTHORIZATION_HEADER = "Authorization";
  private final static String AUTHORIZATION_PREFIX = "KakaoAK ";


  @Bean
  public RequestInterceptor requestInterceptorForKakao() {
    return requestTemplate -> {
      requestTemplate.header(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + authorization);
    };
  }
}
