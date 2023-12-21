package com.topy.bookreview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.api.controller.AuthController;
import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.dto.SignUpResponseDto;
import com.topy.bookreview.api.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
}