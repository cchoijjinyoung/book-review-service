package com.topy.bookreview.global.event;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.type.NotificationType;
import com.topy.bookreview.api.domain.entity.type.TargetType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewLikedEvent {

  private NotificationType notificationType;
  private Member caller;
  private Member receiver;
  private TargetType targetType;
  private Long targetId;
  private String content;
}
