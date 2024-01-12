package com.topy.bookreview.api.dto;

import com.topy.bookreview.api.domain.entity.Notification;
import com.topy.bookreview.api.domain.entity.type.TargetType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

  private Long id;
  private String content;
  private boolean readStatus;
  private String redirectUrl;
  private LocalDateTime createdAt;


  public static NotificationResponseDto fromEntity(Notification notification) {
    return NotificationResponseDto.builder()
        .id(notification.getId())
        .content(notification.getContent())
        .readStatus(notification.getReadAt() != null)
        .redirectUrl(createRedirectUrl(notification.getTargetType(), notification.getTargetId()))
        .createdAt(notification.getCreatedAt())
        .build();
  }

  // 알림 클릭 시 이동하는 url 생성
  private static String createRedirectUrl(TargetType targetType, Long targetId) {
    return targetType.getMappingUrl() + "/" + targetId;
  }
}
