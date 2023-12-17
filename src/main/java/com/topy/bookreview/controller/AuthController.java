package com.topy.bookreview.controller;

import com.topy.bookreview.dto.SignUpRequestDto;
import com.topy.bookreview.service.AuthService;
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
  public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDtos) {
    return ResponseEntity.ok(authService.signUp(signUpRequestDtos));
  }

  @GetMapping("/auth/verify")
  public String verify(@RequestParam String email, String authCode) {
    authService.verify(email, authCode);
    return "인증 완료";
  }
}
