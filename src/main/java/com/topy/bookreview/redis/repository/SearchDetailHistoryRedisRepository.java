package com.topy.bookreview.redis.repository;

import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.redis.RedisManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchDetailHistoryRedisRepository {

  private final RedisManager redisManager;

  private final static String KEY_PREFIX = "ISBN:";

  private final static long EXPIRY_TIME_MILLIS = 1000 * 60 * 60;

  public void save(String isbn, BookSearchResponseDto result) {
    redisManager.save(KEY_PREFIX + isbn, result, EXPIRY_TIME_MILLIS);
  }

  public BookSearchResponseDto get(String isbn) {
    return redisManager.get(KEY_PREFIX + isbn, BookSearchResponseDto.class);
  }

  /**
   * @return long - TimeUnit.MILLISECONDS
   */
  public Long getExpire(String isbn) {
    return redisManager.getExpire(KEY_PREFIX + isbn, TimeUnit.MILLISECONDS);
  }

  public boolean delete(String isbn) {
    return redisManager.delete(KEY_PREFIX + isbn);
  }
}
