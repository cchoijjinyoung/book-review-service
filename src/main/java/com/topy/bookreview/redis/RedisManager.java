package com.topy.bookreview.redis;

import static com.topy.bookreview.global.exception.ErrorCode.CACHE_DATA_CONVERT_ERROR;

import com.topy.bookreview.global.exception.CustomException;
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

  public <T> T get(String key, Class<T> type) {
    Object value = redisTemplate.opsForValue().get(key);
    if (value == null) {
      return null;
    }
    if (type.isInstance(value)) {
      return type.cast(value);
    } else {
      throw new CustomException(CACHE_DATA_CONVERT_ERROR);
    }
  }

  public Long getExpire(String key, TimeUnit timeUnit) {
    return redisTemplate.getExpire(key, timeUnit);
  }

  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

  public void publish(String channelName, Object message) {
    redisTemplate.convertAndSend(channelName, message);
  }
}
