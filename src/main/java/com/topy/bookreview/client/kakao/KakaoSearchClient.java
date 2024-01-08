package com.topy.bookreview.client.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-search-client", url = "https://dapi.kakao.com", configuration = KakaoSearchClientConfig.class)
public interface KakaoSearchClient {

  @GetMapping("/v3/search/book")
  KakaoBookSearchResponse search(@SpringQueryMap KakaoBookSearchRequest kakaoBookSearchRequest);

  @GetMapping("/v3/search/book?target=isbn")
  KakaoBookSearchResponse searchDetail(@RequestParam String query);
}
