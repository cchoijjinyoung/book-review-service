package com.topy.bookreview.client.naver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "naver-search-client", url = "https://openapi.naver.com", configuration = NaverSearchClientConfig.class)
public interface NaverSearchClient {

  @GetMapping("/v1/search/book.json")
  NaverBookSearchResponse search(@SpringQueryMap NaverBookSearchRequest naverBookSearchRequest);
}
