package com.topy.bookreview.api.domain.repository;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.global.config.JpaAuditingConfig;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ReviewRepositoryTest {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("리뷰 생성 테스트")
  void testReviewCreate() {
    // given
    LocalDateTime emailVerifiedDate = LocalDateTime.now();

    Member author = memberRepository.save(Member.builder()
        .email("author@gmail.com")
        .password("author-password")
        .nickname("작성자닉네임")
        .role(RoleType.USER)
        .emailVerifiedAt(emailVerifiedDate)
        .build());

    Review review = Review.builder()
        .author(author)
        .text("리뷰 내용 입니다.")
        .isbn("978-1-234-56789-0")
        .rating(5)
        .build();

    // when
    Review savedReview = reviewRepository.save(review);

    // then
    Assertions.assertThat(savedReview.getAuthor().getNickname()).isEqualTo("작성자닉네임");
    Assertions.assertThat(savedReview.getText()).isEqualTo("리뷰 내용 입니다.");
    Assertions.assertThat(savedReview.getIsbn()).isEqualTo("978-1-234-56789-0");
    Assertions.assertThat(savedReview.getRating()).isEqualTo(5);
  }
}