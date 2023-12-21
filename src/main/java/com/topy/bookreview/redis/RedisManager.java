package com.topy.bookreview.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisManager {

  private final RedisTemplate<String, Object> redisTemplate;

  public void save(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void save(String key, Object value, long expiryMillis) {
    redisTemplate.opsForValue().set(key, value);
    redisTemplate.expire(key, expiryMillis, TimeUnit.MILLISECONDS);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public Long getExpire(String key, TimeUnit timeUnit) {
    return redisTemplate.getExpire(key, timeUnit);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}
