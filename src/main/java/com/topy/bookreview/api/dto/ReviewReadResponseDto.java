package com.topy.bookreview.api.dto;

import com.topy.bookreview.api.domain.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReadResponseDto {
  private Long reviewId;

  private Long userId;

  private String nickname;

  private String content;

  private String isbn;

  private long likeCount;

  private int rating;

  public static ReviewReadResponseDto fromEntity(Review findReview) {
    return ReviewReadResponseDto.builder()
        .reviewId(findReview.getId())
        .userId(findReview.getAuthor().getId())
        .nickname(findReview.getAuthor().getNickname())
        .content(findReview.getContent())
        .isbn(findReview.getIsbn())
        .likeCount(findReview.getLikeCount())
        .rating(findReview.getRating())
        .build();
  }
}
