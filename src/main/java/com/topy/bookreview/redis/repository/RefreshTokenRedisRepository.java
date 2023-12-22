package com.topy.bookreview.redis.repository;

import com.topy.bookreview.redis.RedisManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

  private final RedisManager redisManager;

  private final static String KEY_PREFIX = "REFRESH_TOKEN:";

  private final static long EXPIRY_TIME_MILLIS = 1000 * 60 * 60 * 24 * 14;

  public void save(String refreshToken) {
    redisManager.save(KEY_PREFIX + refreshToken, refreshToken, EXPIRY_TIME_MILLIS);
  }

  public Object get(String refreshToken) {
    return redisManager.get(KEY_PREFIX + refreshToken);
  }

  /**
   * @return long - TimeUnit.MILLISECONDS
   */
  public Long getExpire(String refreshToken) {
    return redisManager.getExpire(KEY_PREFIX + refreshToken, TimeUnit.MILLISECONDS);
  }

  public boolean delete(String refreshToken) {
    return redisManager.delete(KEY_PREFIX + refreshToken);
  }
}
