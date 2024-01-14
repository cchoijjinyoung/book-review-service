package com.topy.bookreview.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.topy.bookreview.api.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

  private final static String MOCK_USER_EMAIL = "username@example.com";

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private LikeService likeService;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .build();
  }

  @WithMockUser(username = MOCK_USER_EMAIL)
  @Test
  @DisplayName("리뷰 좋아요 성공.")
  void successful_likeReview() throws Exception {
    // given
    Long reviewId = 1L;

    String email = MOCK_USER_EMAIL;

    // when
    when(likeService.likeReview(email, reviewId)).thenReturn(1L);
    // then
    mockMvc.perform(post("/likes/reviews/" + reviewId))
        .andExpect(status().isOk());
  }

  @WithMockUser(username = MOCK_USER_EMAIL)
  @Test
  @DisplayName("리뷰 좋아요 취소 성공.")
  void successful_likeCancelReview() throws Exception {
    // given
    Long reviewId = 1L;

    String email = MOCK_USER_EMAIL;

    // when
    when(likeService.likeReview(email, reviewId)).thenReturn(1L);
    // then
    mockMvc.perform(delete("/likes/reviews/" + reviewId))
        .andExpect(status().isOk());
  }
}