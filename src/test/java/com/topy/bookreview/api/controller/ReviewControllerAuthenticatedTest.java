package com.topy.bookreview.api.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewCreateResponseDto;
import com.topy.bookreview.api.dto.ReviewUpdateRequestDto;
import com.topy.bookreview.api.dto.ReviewUpdateResponseDto;
import com.topy.bookreview.api.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
class ReviewControllerAuthenticatedTest {

  private final static String MOCK_USER_EMAIL = "username@example.com";

  @MockBean
  private ReviewService reviewService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @WithMockUser(username = MOCK_USER_EMAIL)
  @Test
  @DisplayName("리뷰 작성 성공")
  void successful_createReview() throws Exception {
    // given
    ReviewCreateRequestDto request = ReviewCreateRequestDto.builder()
        .content("내용입니다.")
        .isbn("1234567890123")
        .rating(5)
        .build();

    ReviewCreateResponseDto response = new ReviewCreateResponseDto(1L);

    given(reviewService.saveReview(MOCK_USER_EMAIL, request)).willReturn(response);

    mockMvc.perform(
            post("/reviews")
                .with(csrf())
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @WithMockUser(username = MOCK_USER_EMAIL)
  @Test
  @DisplayName("리뷰를 리뷰아이디로 수정할 수 있다.")
  void successful_updateReview() throws Exception {
    // given
    ReviewUpdateRequestDto request = ReviewUpdateRequestDto.builder()
        .content("수정한 내용입니다.")
        .rating(4)
        .build();

    Long reviewId = 1L;

    ReviewUpdateResponseDto response = new ReviewUpdateResponseDto(reviewId);

    given(reviewService.editReview(MOCK_USER_EMAIL, reviewId, request)).willReturn(response);

    mockMvc.perform(
            patch("/reviews/" + reviewId)
                .with(csrf())
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = MOCK_USER_EMAIL)
  @Test
  @DisplayName("리뷰를 리뷰아이디로 삭제할 수 있다.")
  void successful_deleteReview() throws Exception {
    // given
    Long reviewId = 1L;

    mockMvc.perform(
            delete("/reviews/" + reviewId)
                .with(csrf())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}