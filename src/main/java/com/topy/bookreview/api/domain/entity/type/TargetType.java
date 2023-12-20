package com.topy.bookreview.api.domain.entity.type;

/**
 * 알림을 클릭했을 때, 볼 수 있는 정보 e.g. ['유저1'님이 '리뷰1'을 좋아합니다.] 라는 알림이 왔을 때, 위 알림의 Target 은 '리뷰1'이다. =>
 * TargetType.REVIEW
 */
public enum TargetType {
  REVIEW
}
