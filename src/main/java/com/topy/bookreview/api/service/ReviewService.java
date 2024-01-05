package com.topy.bookreview.api.service;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.domain.repository.ReviewRepository;
import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewCreateResponseDto;
import com.topy.bookreview.api.dto.ReviewListRequestDto;
import com.topy.bookreview.api.dto.ReviewReadResponseDto;
import com.topy.bookreview.api.dto.ReviewUpdateRequestDto;
import com.topy.bookreview.api.dto.ReviewUpdateResponseDto;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final MemberRepository memberRepository;
  private final ReviewRepository reviewRepository;

  @Transactional
  public ReviewCreateResponseDto saveReview(String email,
      ReviewCreateRequestDto reviewCreateRequestDto) {

    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Review review = Review.builder()
        .author(findMember)
        .isbn(reviewCreateRequestDto.getIsbn())
        .content(reviewCreateRequestDto.getContent())
        .rating(reviewCreateRequestDto.getRating())
        .build();

    Review savedReview = reviewRepository.save(review);
    return new ReviewCreateResponseDto(savedReview.getId());
  }

  @Transactional(readOnly = true)
  public ReviewReadResponseDto findReviewById(Long id) {
    Review findReview = reviewRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    return ReviewReadResponseDto.fromEntity(findReview);
  }

  @Transactional(readOnly = true)
  public Slice<ReviewReadResponseDto> findReviewsByIsbn(ReviewListRequestDto reviewListRequestDto) {
    Pageable pageable = PageRequest.of(
        reviewListRequestDto.getPage(),
        reviewListRequestDto.getSize(),
        Sort.by(Direction.DESC, reviewListRequestDto.getSort()));

    Slice<Review> reviews = reviewRepository.findByIsbn(reviewListRequestDto.getIsbn(), pageable);
    return reviews.map(ReviewReadResponseDto::fromEntity);
  }

  @Transactional
  public ReviewUpdateResponseDto editReview(String email, Long id,
      ReviewUpdateRequestDto reviewUpdateRequestDto) {

    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Review findReview = reviewRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    if (!Objects.equals(findReview.getAuthor().getId(), findMember.getId())) {
      throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }
    findReview.update(reviewUpdateRequestDto);
    return new ReviewUpdateResponseDto(findReview.getId());
  }

  @Transactional
  public void deleteReview(String email, Long id) {
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Review findReview = reviewRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    if (!Objects.equals(findReview.getAuthor().getId(), findMember.getId())) {
      throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    reviewRepository.delete(findReview);
  }
}
