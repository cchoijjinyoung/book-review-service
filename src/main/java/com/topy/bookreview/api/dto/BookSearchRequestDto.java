package com.topy.bookreview.api.dto;

import static com.topy.bookreview.api.constant.SortType.ACCURACY;
import static com.topy.bookreview.api.constant.SortType.LATEST;

import com.topy.bookreview.api.constant.SortType;
import lombok.Getter;

@Getter
public class BookSearchRequestDto {

  private final String keyword;

  private final Integer page;

  private final Integer size;

  private final SortType sortType;

  public BookSearchRequestDto(String keyword, Integer page, Integer size, String sort) {
    this.keyword = keyword;
    this.size = size < 10 || size > 50 ? 10 : size;
    this.page = page;
    this.sortType = resolveSort(sort);
  }

  private SortType resolveSort(String sort) {
    if (LATEST.getDefaultValue().equals(sort)) {
      return LATEST;
    } else {
      return ACCURACY;
    }
  }
}
