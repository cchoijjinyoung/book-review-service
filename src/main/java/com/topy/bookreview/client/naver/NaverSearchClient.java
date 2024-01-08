package com.topy.bookreview.client.naver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-search-client", url = "https://openapi.naver.com", configuration = NaverSearchClientConfig.class)
public interface NaverSearchClient {

  @GetMapping("/v1/search/book.json")
  NaverBookSearchResponse search(@SpringQueryMap NaverBookSearchRequest naverBookSearchRequest);

  @GetMapping("/v1/search/book_adv.json")
  NaverBookSearchResponse searchDetail(@RequestParam String d_isbn);
}
