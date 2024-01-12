package com.topy.bookreview.api.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Notification;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.entity.type.NotificationType;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.api.domain.entity.type.TargetType;
import com.topy.bookreview.global.config.JpaAuditingConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Test
  @DisplayName("알림 생성 테스트")
  void testNotificationCreate() {
    // given
    LocalDateTime emailVerifiedDate = LocalDateTime.now();

    Member receiver = memberRepository.save(Member.builder()
        .email("receiver@gmail.com")
        .password("receiver-password")
        .nickname("수신자닉네임")
        .role(RoleType.USER)
        .emailVerifiedAt(emailVerifiedDate)
        .build());

    Member caller = memberRepository.save(Member.builder()
        .email("caller@gmail.com")
        .password("caller-password")
        .nickname("발신자닉네임")
        .role(RoleType.USER)
        .emailVerifiedAt(emailVerifiedDate)
        .build());

    Review review = reviewRepository.save(Review.builder()
        .author(receiver)
        .content("리뷰 내용 입니다.")
        .isbn("978-1-234-56789-0")
        .rating(5)
        .build());

    Notification notification = Notification.builder()
        .receiver(receiver)
        .caller(caller)
        .content("알림 내용입니다.")
        .notificationType(NotificationType.LIKE)
        .targetType(TargetType.REVIEW)
        .targetId(review.getId())
        .build();
    // when
    Notification savedNotification = notificationRepository.save(notification);

    // then
    assertThat(savedNotification.getReceiver().getNickname()).isEqualTo("수신자닉네임");
    assertThat(savedNotification.getCaller().getNickname()).isEqualTo("발신자닉네임");
    assertThat(savedNotification.getNotificationType()).isEqualTo(NotificationType.LIKE);
    assertThat(savedNotification.getContent()).isEqualTo("알림 내용입니다.");
    assertThat(savedNotification.getTargetType()).isEqualTo(TargetType.REVIEW);
    assertThat(savedNotification.getTargetId()).isEqualTo(review.getId());
    assertThat(savedNotification.getReadAt()).isNull();
  }
}