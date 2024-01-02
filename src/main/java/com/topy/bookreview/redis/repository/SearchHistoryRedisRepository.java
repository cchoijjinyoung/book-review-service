package com.topy.bookreview.redis.repository;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.redis.RedisManager;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchHistoryRedisRepository {

  private final RedisManager redisManager;

  private final static String KEYWORD_PREFIX = "KEYWORD:";
  private final static String PAGE_PREFIX = "PAGE:";
  private final static String SIZE_PREFIX = "SIZE:";
  private final static String SORT_PREFIX = "SORT:";

  private final static long EXPIRY_TIME_MILLIS = 1000 * 60 * 60;

  public void save(BookSearchRequestDto request, Object result) {
    redisManager.save(
        KEYWORD_PREFIX + request.getKeyword()
        + PAGE_PREFIX + request.getPage()
        + SIZE_PREFIX + request.getSize()
        + SORT_PREFIX + request.getSortType(), result, EXPIRY_TIME_MILLIS);

  }

  public List<BookSearchResponseDto> get(BookSearchRequestDto request) {
    return (List<BookSearchResponseDto>) redisManager.get(
        KEYWORD_PREFIX + request.getKeyword()
        + PAGE_PREFIX + request.getPage()
        + SIZE_PREFIX + request.getSize()
        + SORT_PREFIX + request.getSortType());
  }

  /**
   * @return long - TimeUnit.MILLISECONDS
   */
  public Long getExpire(BookSearchRequestDto request) {
    return redisManager.getExpire(
        KEYWORD_PREFIX + request.getKeyword()
        + PAGE_PREFIX + request.getPage()
        + SIZE_PREFIX + request.getSize()
        + SORT_PREFIX + request.getSortType(), TimeUnit.MILLISECONDS);
  }

  public boolean delete(BookSearchRequestDto request) {
    return redisManager.delete(
        KEYWORD_PREFIX + request.getKeyword()
        + PAGE_PREFIX + request.getPage()
        + SIZE_PREFIX + request.getSize()
        + SORT_PREFIX + request.getSortType());
  }
}