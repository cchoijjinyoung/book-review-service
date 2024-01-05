package com.topy.bookreview.api.controller;

import static com.topy.bookreview.api.constant.ReviewSortType.LATEST;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.topy.bookreview.api.dto.ReviewListRequestDto;
import com.topy.bookreview.api.dto.ReviewReadResponseDto;
import com.topy.bookreview.api.service.ReviewService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ReviewController.class,
    excludeAutoConfiguration = {
        UserDetailsServiceAutoConfiguration.class,
        SecurityAutoConfiguration.class
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                SecurityConfig.class, AuthenticationFilter.class
            })
    }
)
class ReviewControllerPermitRequest2Test {

  @MockBean
  private ReviewService reviewService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("책 isbn에 해당하는 리뷰 조회 성공")
  void successful_getReviewByIsbn() throws Exception {
    // given
    ReviewListRequestDto request = ReviewListRequestDto.builder()
        .isbn("1234567890123")
        .page(1)
        .size(10)
        .sort(LATEST.getValue())
        .build();

    List<ReviewReadResponseDto> storedReviews = List.of(
        ReviewReadResponseDto.builder()
            .reviewId(1L)
            .rating(1)
            .content("내용입니다1.")
            .build(),
        ReviewReadResponseDto.builder()
            .reviewId(2L)
            .rating(2)
            .content("내용입니다2.")
            .build(),
        ReviewReadResponseDto.builder()
            .reviewId(3L)
            .rating(3)
            .content("내용입니다3.")
            .build()
    );

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
    Slice<ReviewReadResponseDto> response = new SliceImpl<>(storedReviews, pageRequest, true);

    given(reviewService.findReviewsByIsbn(request)).willReturn(response);

    mockMvc.perform(
            get("/reviews")
                .param("isbn", request.getIsbn())
                .param("page", String.valueOf(request.getPage()))
                .param("size", String.valueOf(request.getSize()))
                .param("sort", request.getSort())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("리뷰를 리뷰아이디로 조회할 수 있다.")
  void successful_getReviewById() throws Exception {
    // given
    Long reviewId = 1L;

    ReviewReadResponseDto response = ReviewReadResponseDto.builder()
        .reviewId(1L)
        .rating(1)
        .content("내용입니다1.")
        .build();

    given(reviewService.findReviewById(reviewId)).willReturn(response);

    mockMvc.perform(
            get("/reviews/" + reviewId)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}