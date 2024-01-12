package com.topy.bookreview.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
  CHANNEL_NOTIFICATION("notification:"),
  ;

  private final String prefix;
}
