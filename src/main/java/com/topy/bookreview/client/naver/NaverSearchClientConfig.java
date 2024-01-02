package com.topy.bookreview.client.naver;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverSearchClientConfig {

  @Value("${naver.search.client.id}")
  private String clientId;

  @Value("${naver.search.client.secret}")
  private String clientSecret;

  @Bean
  public RequestInterceptor requestInterceptorForNaver() {
    return requestTemplate -> {
      requestTemplate.header("X-Naver-Client-Id", clientId);
      requestTemplate.header("X-Naver-Client-Secret", clientSecret);
    };
  }
}
