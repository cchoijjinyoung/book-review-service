package com.topy.bookreview.api.controller;

import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewUpdateRequestDto;
import com.topy.bookreview.api.service.ReviewService;
import com.topy.bookreview.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  @PostMapping("/review")
  public ResponseEntity<?> createReview(@AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid ReviewCreateRequestDto reviewCreateRequestDto) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(reviewService.saveReview(email, reviewCreateRequestDto));
  }

  @GetMapping("/review/{isbn}")
  public ResponseEntity<?> getReviewsByIsbn(@PathVariable String isbn, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "createdAt"));
    return ResponseEntity.ok(reviewService.findReviewsByIsbn(isbn, pageable));
  }

  @GetMapping("/review/{id}")
  public ResponseEntity<?> getReviewById(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.findReviewById(id));
  }

  @PreAuthorize("hasRole('USER')")
  @PatchMapping("/review/{id}")
  public ResponseEntity<?> updateReview(@AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long id, @RequestBody ReviewUpdateRequestDto reviewUpdateRequestDto) {
    String email = userDetails.getUsername();
    return ResponseEntity.ok(reviewService.editReview(email, id, reviewUpdateRequestDto));
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/review/{id}")
  public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long id) {
    reviewService.deleteReview(userDetails.getUsername(), id);
    return ResponseEntity.ok().build();
  }
}
