package com.topy.bookreview.api.dto;

import static com.topy.bookreview.api.constant.ReviewSortType.LATEST;
import static com.topy.bookreview.api.constant.ReviewSortType.LIKES;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewListRequestDto {

  private String isbn;

  private Integer page;

  private Integer size;

  private String sort;

  @Builder
  private ReviewListRequestDto(String isbn, Integer page, Integer size, String sort) {
    this.isbn = isbn;
    this.page = (page == null ? 1 : page) - 1;
    this.size = size == null ? 10 : size;
    this.sort = LIKES.getValue().equals(sort) ? LIKES.getValue() : LATEST.getValue();
  }
}
