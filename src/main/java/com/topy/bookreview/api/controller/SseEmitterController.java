package com.topy.bookreview.api.controller;

import com.topy.bookreview.api.service.SseEmitterService;
import com.topy.bookreview.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseEmitterController {

  private final SseEmitterService sseEmitterService;

  @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getId();
    return sseEmitterService.connect(userId);
  }
}
