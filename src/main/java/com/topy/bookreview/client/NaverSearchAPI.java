package com.topy.bookreview.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 작성 중인 클래스입니다.
 */
@Component
public class NaverSearchAPI {

  @Value("${search.client.id}")
  private String clientId;

  @Value("${search.client.secret}")
  private String clientSecret;

  private final static String BASE_URL = "https://openapi.naver.com";

  private final static String BOOK_PATH = "/v1/search/book.json";

  private final static String CLIENT_ID_HEADER = "X-Naver-Client-Id";
  private final static String CLIENT_SECRET_HEADER = "X-Naver-Client-Secret";


  WebClient webClient = WebClient.builder()
      .baseUrl(BASE_URL)
      .defaultHeader(CLIENT_ID_HEADER, clientId)
      .defaultHeader(CLIENT_SECRET_HEADER, clientSecret)
      .build();

  public String search(String keyword) {
    WebClient.ResponseSpec responseSpec = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(BOOK_PATH)
            .queryParam("query", keyword)
            .build())
        .retrieve();
    return responseSpec.bodyToMono(String.class).block();
  }
}
