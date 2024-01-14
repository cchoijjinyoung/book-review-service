package com.topy.bookreview.api.service;

import static com.topy.bookreview.global.exception.ErrorCode.SEARCH_RESULT_EMPTY;

import com.topy.bookreview.api.component.SearchManager;
import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.global.event.SearchDetailHistorySaveEvent;
import com.topy.bookreview.global.event.SearchHistorySaveEvent;
import com.topy.bookreview.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final SearchManager searchManager;
  private final ApplicationEventPublisher eventPublisher;

  public List<BookSearchResponseDto> search(BookSearchRequestDto bookSearchRequestDto) {
    List<BookSearchResponseDto> result = searchManager.searchActual(bookSearchRequestDto);
    if (result.isEmpty()) {
      throw new CustomException(SEARCH_RESULT_EMPTY);
    }
    eventPublisher.publishEvent(new SearchHistorySaveEvent(bookSearchRequestDto, result));
    return result;
  }


  public BookSearchResponseDto searchBookByIsbn(String isbn) {
    BookSearchResponseDto result = searchManager.searchDetailActual(isbn);
    if (result == null) {
      throw new CustomException(SEARCH_RESULT_EMPTY);
    }
    eventPublisher.publishEvent(new SearchDetailHistorySaveEvent(isbn, result));
    return result;
  }
}
