package com.topy.bookreview.api.service;

import static com.topy.bookreview.api.domain.entity.type.NotificationType.LIKE;
import static com.topy.bookreview.api.domain.entity.type.TargetType.REVIEW;
import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EXIST_LIKE;
import static com.topy.bookreview.global.exception.ErrorCode.LIKE_NOT_FOUND;

import com.topy.bookreview.api.domain.entity.Like;
import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.repository.LikeRepository;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.domain.repository.ReviewRepository;
import com.topy.bookreview.global.event.ReviewLikedEvent;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final MemberRepository memberRepository;
  private final ReviewRepository reviewRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public long likeReview(String email, Long reviewId) {
    Member caller = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    if (likeRepository.findByMemberAndReview(caller, review).isPresent()) {
      throw new CustomException(ALREADY_EXIST_LIKE);
    }

    likeRepository.save(
        Like.builder()
            .member(caller)
            .review(review)
            .build());
    long likeCount = review.increaseLikeCount();

    publishReviewLikedEvent(reviewId, caller, review);

    return likeCount;
  }

  private void publishReviewLikedEvent(Long reviewId, Member caller, Review review) {
    eventPublisher.publishEvent(ReviewLikedEvent.builder()
        .notificationType(LIKE)
        .caller(caller)
        .receiver(review.getAuthor())
        .targetType(REVIEW)
        .targetId(reviewId)
        .content(String.format("%s님이 회원님의 리뷰를 좋아합니다", caller))
        .build());
  }

  @Transactional
  public long likeCancelReview(String email, Long reviewId) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    Like like = likeRepository.findByMemberAndReview(member, review)
        .orElseThrow(() -> new CustomException(LIKE_NOT_FOUND));

    likeRepository.delete(like);

    return review.decreaseLikeCount();
  }
}
