package com.topy.bookreview.global.event;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchHistorySaveEvent {

  private BookSearchRequestDto bookSearchRequestDto;
  private List<BookSearchResponseDto> result;
}
