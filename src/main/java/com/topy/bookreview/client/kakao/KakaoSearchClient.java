package com.topy.bookreview.client.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "kakao-search-client", url = "https://dapi.kakao.com", configuration = KakaoSearchClientConfig.class)
public interface KakaoSearchClient {

  @GetMapping("/v3/search/book")
  KakaoBookSearchResponse search(@SpringQueryMap KakaoBookSearchRequest kakaoBookSearchRequest);
}
