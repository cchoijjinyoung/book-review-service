package com.topy.bookreview.api.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

  public SseEmitter save(String id, SseEmitter sseEmitter) {
    sseEmitters.put(id, sseEmitter);
    return sseEmitter;
  }

  public Optional<SseEmitter> get(String key) {
    return Optional.ofNullable(sseEmitters.get(key));
  }

  public void deleteById(String id) {
    sseEmitters.remove(id);
  }
}
