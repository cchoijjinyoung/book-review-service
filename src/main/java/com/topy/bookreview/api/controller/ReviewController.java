package com.topy.bookreview.api.controller;

import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.service.ReviewService;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/review")
  public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(reviewService.create(email, reviewCreateRequestDto));
  }
}
