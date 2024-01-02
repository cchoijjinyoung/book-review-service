package com.topy.bookreview.api.controller;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/book/search")
  public ResponseEntity<?> search(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "accuracy") String sort) {
    BookSearchRequestDto searchRequest = new BookSearchRequestDto(keyword, page, size, sort);
    return ResponseEntity.ok(bookService.search(searchRequest));
  }
}
