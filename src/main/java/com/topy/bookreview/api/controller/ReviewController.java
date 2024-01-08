package com.topy.bookreview.api.controller;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewListRequestDto;
import com.topy.bookreview.api.dto.ReviewUpdateRequestDto;
import com.topy.bookreview.api.service.ReviewService;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/reviews")
  public ResponseEntity<?> createReview(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto) {
    String email = userDetails.getUsername();
    return ResponseEntity.status(SC_CREATED)
        .body(reviewService.saveReview(email, reviewCreateRequestDto));
  }

  @GetMapping("/reviews")
  public ResponseEntity<?> getReviewsByIsbn(
      @ModelAttribute ReviewListRequestDto reviewListRequestDto) {
    return ResponseEntity.ok(reviewService.findReviewsByIsbn(reviewListRequestDto));
  }

  @GetMapping("/reviews/{id}")
  public ResponseEntity<?> getReviewById(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.findReviewById(id));
  }

  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/reviews/{id}")
  public ResponseEntity<?> updateReview(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long id, @RequestBody ReviewUpdateRequestDto reviewUpdateRequestDto) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(reviewService.editReview(email, id, reviewUpdateRequestDto));
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/reviews/{id}")
  public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long id) {
    reviewService.deleteReview(userDetails.getUsername(), id);
    return ResponseEntity.ok().build();
  }
}
