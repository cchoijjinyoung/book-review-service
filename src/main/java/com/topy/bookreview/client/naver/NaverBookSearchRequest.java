package com.topy.bookreview.client.naver;

import static com.topy.bookreview.api.constant.SortType.ACCURACY;
import static com.topy.bookreview.api.constant.SortType.LATEST;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class NaverBookSearchRequest {

  private String query;

  private int display;

  private int start;

  private String sort;

  public static NaverBookSearchRequest of(BookSearchRequestDto bookSearchRequestDto) {
    int display = bookSearchRequestDto.getSize();
    int page = bookSearchRequestDto.getPage();
    int start = display * (page - 1) + 1;

    return NaverBookSearchRequest.builder()
        .query(bookSearchRequestDto.getKeyword())
        .display(bookSearchRequestDto.getSize())
        .start(start)
        .sort(bookSearchRequestDto.getSortType() == LATEST ? LATEST.getNaverValue() : ACCURACY.getNaverValue())
        .build();
  }
}
