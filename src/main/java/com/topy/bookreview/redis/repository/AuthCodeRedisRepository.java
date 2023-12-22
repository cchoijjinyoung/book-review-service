package com.topy.bookreview.redis.repository;

import com.topy.bookreview.redis.RedisManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthCodeRedisRepository {

  private final RedisManager redisManager;

  private final static String KEY_PREFIX = "EMAIL:";

  private final static long EXPIRY_TIME_MILLIS = 1000 * 60 * 60 * 24 * 3;

  public void saveByEmail(String email, String authCode) {
    redisManager.save(KEY_PREFIX + email, authCode, EXPIRY_TIME_MILLIS);
  }

  public Object getByEmail(String email) {
    return redisManager.get(KEY_PREFIX + email);
  }

  /**
   * @return long - TimeUnit.MILLISECONDS
   */
  public Long getExpireByEmail(String email) {
    return redisManager.getExpire(KEY_PREFIX + email, TimeUnit.MILLISECONDS);
  }

  public boolean deleteByEmail(String email) {
    return redisManager.delete(KEY_PREFIX + email);
  }
}
