package com.topy.bookreview.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDto {

  @NotBlank(message = "책의 isbn을 알 수 없습니다.")
  private String isbn;

  @Size(max = 500, message = "내용은 최대 500자까지 가능합니다.")
  @NotNull(message = "내용을 입력해주세요.")
  private String content;

  @Min(value = 0, message = "별점은 (1~5)점 까지만 가능합니다.")
  @Max(value = 5, message = "별점은 (1~5)점 까지만 가능합니다.")
  private int rating;

}
