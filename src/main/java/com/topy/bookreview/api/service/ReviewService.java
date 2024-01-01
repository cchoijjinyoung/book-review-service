package com.topy.bookreview.api.service;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.domain.repository.ReviewRepository;
import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewCreateResponseDto;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final MemberRepository memberRepository;
  private final ReviewRepository reviewRepository;

  @Transactional
  public ReviewCreateResponseDto create(String email, ReviewCreateRequestDto reviewCreateRequestDto) {
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
}
