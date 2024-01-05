package com.topy.bookreview.client.kakao;

import static com.topy.bookreview.api.constant.BookSearchSortType.ACCURACY;
import static com.topy.bookreview.api.constant.BookSearchSortType.LATEST;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class KakaoBookSearchRequest {
  private String query;

  private int page;

  private int size;

  private String sort;

  public static KakaoBookSearchRequest of(BookSearchRequestDto bookSearchRequestDto) {
    return KakaoBookSearchRequest.builder()
        .query(bookSearchRequestDto.getKeyword())
        .page(bookSearchRequestDto.getPage())
        .size(bookSearchRequestDto.getSize())
        .sort(bookSearchRequestDto.getBookSearchSortType() == LATEST ? LATEST.getKakaoValue() : ACCURACY.getKakaoValue())
        .build();
  }
}
