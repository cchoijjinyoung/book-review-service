package com.topy.bookreview.api.controller;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/signup")
  public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
    return ResponseEntity.status(SC_CREATED).body(authService.signUp(signUpRequestDto));
  }

  @GetMapping("/auth/mail/verify")
  public ResponseEntity<?> mailVerify(@RequestParam String email, String authCode, long timeStamp) {
    authService.mailVerify(email, authCode, timeStamp);
    return ResponseEntity.ok("인증이 완료됐습니다.");
  }
}
