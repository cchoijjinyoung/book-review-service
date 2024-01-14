package com.topy.bookreview.global.event.listener;

import com.topy.bookreview.global.event.SearchDetailHistorySaveEvent;
import com.topy.bookreview.global.event.SearchHistorySaveEvent;
import com.topy.bookreview.redis.repository.SearchDetailHistoryRedisRepository;
import com.topy.bookreview.redis.repository.SearchHistoryRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEventListener {

  private final SearchHistoryRedisRepository searchHistoryRedisRepository;
  private final SearchDetailHistoryRedisRepository searchDetailHistoryRedisRepository;

  @EventListener
  public void handleSaveSearchCacheEvent(SearchHistorySaveEvent event) {
    log.info("handleSaveSearchCacheEvent 호출");
    if (ObjectUtils.isEmpty(searchHistoryRedisRepository.get(event.getBookSearchRequestDto()))) {
      searchHistoryRedisRepository.save(event.getBookSearchRequestDto(), event.getResult());
      log.info("책 검색결과를 캐시에 저장하였습니다.");
    }
  }

  @EventListener
  public void handleSaveSearchDetailCacheEvent(SearchDetailHistorySaveEvent event) {
    log.info("handleSaveSearchDetailCacheEvent 호출");
    if (ObjectUtils.isEmpty(searchDetailHistoryRedisRepository.get(event.getIsbn()))) {
      searchDetailHistoryRedisRepository.save(event.getIsbn(), event.getResult());
      log.info("책 상세 검색결과를 캐시에 저장하였습니다.");
    }
  }
}
