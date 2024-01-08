package com.topy.bookreview.api.service;

import static com.topy.bookreview.global.exception.ErrorCode.SEARCH_RESULT_EMPTY;

import com.topy.bookreview.api.component.SearchManager;
import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.global.event.SearchDetailHistorySaveEvent;
import com.topy.bookreview.global.event.SearchHistorySaveEvent;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.redis.repository.SearchDetailHistoryRedisRepository;
import com.topy.bookreview.redis.repository.SearchHistoryRedisRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

  private final SearchManager searchManager;
  private final ApplicationEventPublisher eventPublisher;
  private final SearchHistoryRedisRepository searchHistoryRedisRepository;
  private final SearchDetailHistoryRedisRepository searchDetailHistoryRedisRepository;

  public List<BookSearchResponseDto> search(BookSearchRequestDto bookSearchRequestDto) {
    List<BookSearchResponseDto> result = searchManager.searchActual(bookSearchRequestDto);
    if (result.isEmpty()) {
      throw new CustomException(SEARCH_RESULT_EMPTY);
    }
    eventPublisher.publishEvent(new SearchHistorySaveEvent(bookSearchRequestDto, result));
    return result;
  }

  @EventListener
  public void handleSaveSearchCacheEvent(SearchHistorySaveEvent event) {
    log.info("handleSaveSearchCacheEvent 호출");
    if (ObjectUtils.isEmpty(searchHistoryRedisRepository.get(event.getBookSearchRequestDto()))) {
      searchHistoryRedisRepository.save(event.getBookSearchRequestDto(), event.getResult());
      log.info("책 검색결과를 캐시에 저장하였습니다.");
    }
  }

  public BookSearchResponseDto searchBookByIsbn(String isbn) {
    BookSearchResponseDto result = searchManager.searchDetailActual(isbn);
    if (result == null) {
      throw new CustomException(SEARCH_RESULT_EMPTY);
    }
    eventPublisher.publishEvent(new SearchDetailHistorySaveEvent(isbn, result));
    return result;
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
