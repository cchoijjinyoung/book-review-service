package com.topy.bookreview.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReviewSortType {
  LIKES("likeCount"),
  LATEST("createdAt"),
  ;

  private final String value;
}
