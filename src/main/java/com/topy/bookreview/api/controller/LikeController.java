package com.topy.bookreview.api.controller;

import com.topy.bookreview.api.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/likes/reviews/{reviewId}")
  public ResponseEntity<?> likeReview(@PathVariable Long reviewId,
      @AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(likeService.likeReview(email, reviewId));
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/likes/reviews/{reviewId}")
  public ResponseEntity<?> likeCancelReview(@PathVariable Long reviewId,
      @AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(likeService.likeCancelReview(email, reviewId));
  }
}
