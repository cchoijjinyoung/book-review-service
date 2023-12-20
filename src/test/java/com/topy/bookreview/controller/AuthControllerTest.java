package com.topy.bookreview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.api.controller.AuthController;
import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.dto.SignUpResponseDto;
import com.topy.bookreview.api.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @MockBean
  private AuthService authService;

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper mapper = new ObjectMapper();

  private String asJsonString(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }

  @Test
  @DisplayName("회원가입 성공 테스트")
  void signUpTest() throws Exception {
    // given
    given(authService.signUp(any())).willReturn(
        SignUpResponseDto.builder().nickname("nick").build());

    // when
    SignUpRequestDto signUpRequestDto =
        new SignUpRequestDto("foo@gmail.com", "bar", "nick");
    // then
    mockMvc.perform(post("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(signUpRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nickname").value("nick"));
  }

  @Test
  @DisplayName("액세스 토큰 재발급 테스트")
  void reissueAccessTokenTest() throws Exception {
    // given
    String refreshToken = "12345";
    String newAccessToken = "54321";
    // when
    when(authService.reissueAccessToken(refreshToken)).thenReturn(newAccessToken);
    // then
    mockMvc.perform(post("/auth/token/reissue")
            .cookie(new Cookie("refreshToken", refreshToken)))
        .andExpect(status().isOk())
        .andExpect(content().string(newAccessToken));
  }
}