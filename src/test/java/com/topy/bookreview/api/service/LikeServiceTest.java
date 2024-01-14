package com.topy.bookreview.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.topy.bookreview.api.domain.entity.Like;
import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.api.domain.repository.LikeRepository;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.domain.repository.ReviewRepository;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

  @InjectMocks
  private LikeService likeService;

  @Mock
  private LikeRepository likeRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Test
  @DisplayName("❗회원이 아니면 리뷰를 좋아요를 할 수 없다.")
  void fail_likeReview_when_userNotFound() {
    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    given(memberRepository.findByEmail(email)).willThrow(
        new CustomException(ErrorCode.USER_NOT_FOUND));

    // then
    assertThatThrownBy(() -> likeService.likeReview(email, reviewId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("회원을 찾을 수 없습니다.");
    verify(memberRepository, times(1)).findByEmail(email);
    verify(likeRepository, times(0)).save(any(Like.class));
  }

  @Test
  @DisplayName("리뷰에 좋아요를 누를 때 이미 존재하는 좋아요면 실패한다.")
  void fail_likeReview_when_already_exist_like() {
    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member member = Member.builder()
        .email(email)
        .password("1q2w3e4r!")
        .nickname("foo")
        .role(RoleType.USER)
        .build();

    Review review = Review.builder()
        .id(reviewId)
        .build();

    Like response = Like.builder()
        .member(member)
        .review(review)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.ofNullable(member));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(review));
    given(likeRepository.findByMemberAndReview(member, review)).willReturn(
        Optional.ofNullable(response));

    // when
    // then
    assertThatThrownBy(() -> likeService.likeReview(email, reviewId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("이미 해당 리뷰에 '좋아요'를 누른 회원입니다.");
    verify(memberRepository, times(1)).findByEmail(email);
    verify(likeRepository, times(0)).save(any(Like.class));
  }

  @Test
  @DisplayName("리뷰에 좋아요를 누르면 좋아요수가 1 증가한다.")
  void likeReview() {
    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member member = Member.builder()
        .email(email)
        .password("1q2w3e4r!")
        .nickname("foo")
        .role(RoleType.USER)
        .build();

    Review review = Review.builder()
        .id(reviewId)
        .likeCount(0L)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.ofNullable(member));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.ofNullable(review));
    given(likeRepository.findByMemberAndReview(member, review)).willReturn(Optional.empty());

    // when
    long likeCount = likeService.likeReview(email, reviewId);
    // then
    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(likeRepository, times(1)).save(any(Like.class));
    assertThat(likeCount).isEqualTo(1L);
  }
}