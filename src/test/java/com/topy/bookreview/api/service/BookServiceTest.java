package com.topy.bookreview.api.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.topy.bookreview.api.component.SearchManager;
import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.global.event.SearchDetailHistorySaveEvent;
import com.topy.bookreview.global.event.SearchHistorySaveEvent;
import com.topy.bookreview.global.exception.CustomException;
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
  private BookService bookService;

  @Mock
  private SearchManager searchManager;

  @Mock
  private ApplicationEventPublisher eventPublisher;

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
  }

  @Test
  @DisplayName("❗책 상세 검색 시 조회된 검색결과가 없으면 예외를 던진다.")
  void searchBookByIsbnTest_fail_whenSearchResult_is_null() {
    // given
    String isbn = "1234567890123";

    // when
    when(searchManager.searchDetailActual(isbn)).thenReturn(null);
    // then
    assertThatThrownBy(() -> bookService.searchBookByIsbn(isbn))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("조회된 결과가 없습니다.");
  }

  @Test
  @DisplayName("책 상세 검색 성공")
  void searchBookByIsbnTest_success() {
    // given
    String isbn = "1234567890123";
    BookSearchResponseDto response = new BookSearchResponseDto();

    // when
    when(searchManager.searchDetailActual(isbn)).thenReturn(response);

    // then
    BookSearchResponseDto result = bookService.searchBookByIsbn(isbn);

    verify(searchManager, times(1)).searchDetailActual(isbn);
    verify(eventPublisher, times(1)).publishEvent(any(SearchDetailHistorySaveEvent.class));
  }
}
