package com.topy.bookreview.api.service;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

  private final SseEmitterRepository sseEmitterRepository;

  private static final long SSE_EMITTER_TIME_OUT = 1000L * 60 * 60 * 3;

  @Transactional
  public SseEmitter connect(Long userId) {
    String key = String.valueOf(userId);
    SseEmitter sseEmitter = sseEmitterRepository.save(key, new SseEmitter(SSE_EMITTER_TIME_OUT));

    sseEmitter.onCompletion(() -> sseEmitterRepository.deleteById(key));
    sseEmitter.onTimeout(sseEmitter::complete);
    sseEmitter.onError((throwable) -> {
      log.error("SSE emitter onError", throwable);
      sseEmitter.complete();
    });

    // 더미 이벤트 발송
    try {
      sseEmitter.send(event().name("Connect 성공"));
    } catch (IOException e) {
      log.error("SSE dummy send IOException 발생", e);
    }

    return sseEmitter;
  }
}
