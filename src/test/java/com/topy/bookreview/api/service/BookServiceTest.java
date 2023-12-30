package com.topy.bookreview.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.topy.bookreview.api.component.SearchManager;
import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.global.event.SearchHistorySaveEvent;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.redis.repository.SearchHistoryRedisRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @InjectMocks
  BookService bookService;

  @Mock
  SearchManager searchManager;

  @Mock
  ApplicationEventPublisher eventPublisher;

  @Mock
  SearchHistoryRedisRepository searchHistoryRedisRepository;

  @Test
  @DisplayName("❗책 검색 시 조회된 검색결과가 없으면 예외를 던진다.")
  void searchTest_fail_whenSearchResultEmpty() {
    // given
    BookSearchRequestDto request = new BookSearchRequestDto("test", 1, 1, "accuracy");
    List<BookSearchResponseDto> result = List.of();

    // when
    when(searchManager.searchActual(request)).thenReturn(result);
    // then
    assertThatThrownBy(() -> bookService.search(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("조회된 결과가 없습니다.");
  }

  @Test
  @DisplayName("책 검색 성공")
  void searchTest_success() {
    // given
    BookSearchRequestDto request = new BookSearchRequestDto("test", 1, 1, "accuracy");
    BookSearchResponseDto response = new BookSearchResponseDto();
    List<BookSearchResponseDto> responseDtoList = List.of(response);

    // when
    when(searchManager.searchActual(request)).thenReturn(responseDtoList);

    // then
    List<BookSearchResponseDto> result = bookService.search(request);

    verify(searchManager, times(1)).searchActual(any(BookSearchRequestDto.class));
    verify(eventPublisher, times(1)).publishEvent(any(SearchHistorySaveEvent.class));
    assertThat(responseDtoList).isEqualTo(result);
  }

  @Test
  @DisplayName("책 검색 시 검색결과가 캐시에 없으면, 캐시에 저장되어야한다.")
  void handleSaveCacheEventTest_when_emptyCache() {
    // given
    BookSearchRequestDto request = new BookSearchRequestDto("test", 1, 1, "accuracy");
    BookSearchResponseDto response = new BookSearchResponseDto();
    List<BookSearchResponseDto> responseDtoList = List.of(response);
    SearchHistorySaveEvent event = new SearchHistorySaveEvent(request, responseDtoList);

    // when
    when(searchHistoryRedisRepository.get(request)).thenReturn(null);
    doNothing().when(searchHistoryRedisRepository).save(request, responseDtoList);

    // then
    bookService.handleSaveCacheEvent(event);
    verify(searchHistoryRedisRepository, times(1)).get(any(BookSearchRequestDto.class));
    verify(searchHistoryRedisRepository, times(1)).save(any(BookSearchRequestDto.class), any(List.class));
  }

  @Test
  @DisplayName("책 검색 시 검색결과가 캐시에 있으면, 캐시에 저장되지 않는다.")
  void handleSaveCacheEventTest_when_existCache() {
    // given
    BookSearchRequestDto request = new BookSearchRequestDto("test", 1, 1, "accuracy");
    BookSearchResponseDto response = new BookSearchResponseDto();
    List<BookSearchResponseDto> responseDtoList = List.of(response);
    SearchHistorySaveEvent event = new SearchHistorySaveEvent(request, responseDtoList);

    // when
    when(searchHistoryRedisRepository.get(request)).thenReturn(responseDtoList);

    // then
    bookService.handleSaveCacheEvent(event);
    verify(searchHistoryRedisRepository, times(1)).get(any(BookSearchRequestDto.class));
    verify(searchHistoryRedisRepository, never()).save(any(BookSearchRequestDto.class), any(List.class));
  }
}