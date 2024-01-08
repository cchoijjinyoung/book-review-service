package com.topy.bookreview.api.controller;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/signup")
  public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.status(SC_CREATED).body(authService.signUp(signUpRequestDto));
  }

  @GetMapping("/auth/mail/verify")
  public ResponseEntity<?> mailVerify(@RequestParam String email, @RequestParam String authCode) {
    authService.mailVerify(email, authCode);

    return ResponseEntity.ok("인증이 완료됐습니다.");
  }
}
