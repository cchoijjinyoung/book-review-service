package com.topy.bookreview.api.domain.repository;

import com.topy.bookreview.api.domain.entity.Likes;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LikesRepositoryTest {

  @Autowired
  private LikesRepository likesRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Test
  @DisplayName("좋아요 생성 테스트")
  void testLikesCreate() {
    // given
    LocalDateTime emailVerifiedDate = LocalDateTime.now();

    Member author = memberRepository.save(Member.builder()
        .email("author@gmail.com")
        .password("author-password")
        .nickname("작성자닉네임")
        .role(RoleType.USER)
        .emailVerifiedAt(emailVerifiedDate)
        .build());

    Member liker = memberRepository.save(Member.builder()
        .email("liker@gmail.com")
        .password("liker-password")
        .nickname("좋아요닉네임")
        .role(RoleType.USER)
        .emailVerifiedAt(emailVerifiedDate)
        .build());

    Review review = reviewRepository.save(Review.builder()
        .author(author)
        .content("리뷰 내용 입니다.")
        .isbn("978-1-234-56789-0")
        .rating(5)
        .build());

    Likes likes = Likes.builder()
        .review(review)
        .member(liker)
        .build();

    // when
    Likes savedLikes = likesRepository.save(likes);

    // then
    Assertions.assertThat(savedLikes.getReview().getContent()).isEqualTo("리뷰 내용 입니다.");
    Assertions.assertThat(savedLikes.getReview().getIsbn()).isEqualTo("978-1-234-56789-0");
    Assertions.assertThat(savedLikes.getMember().getNickname()).isEqualTo("좋아요닉네임");

  }

}