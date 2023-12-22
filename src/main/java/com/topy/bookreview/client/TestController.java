package com.topy.bookreview.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성 중인 클래스입니다.
 */
@RestController
@RequiredArgsConstructor
public class TestController {

  private final NaverSearchAPI naverSearchAPI;

  @PostMapping("/client/test")
  public String test(@RequestBody String keyword) {
    return naverSearchAPI.search(keyword);
  }

  @GetMapping("/login")
  public String loginPage() {
    return "로그인 페이지입니다.";
  }
}
