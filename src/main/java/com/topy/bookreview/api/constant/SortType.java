package com.topy.bookreview.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
  ACCURACY("accuracy", "sim", "accuracy"),
  LATEST("latest", "date", "latest"),
  ;
  private final String defaultValue;
  private final String naverValue;
  private final String kakaoValue;
}
