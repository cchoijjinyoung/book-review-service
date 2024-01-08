package com.topy.bookreview.api.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.service.BookService;
import com.topy.bookreview.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/books/search")
  public ResponseEntity<?> search(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "accuracy") String sort) {
    BookSearchRequestDto searchRequest = new BookSearchRequestDto(keyword, page, size, sort);
    return ResponseEntity.ok(bookService.search(searchRequest));
  }

  @GetMapping("/books/{isbn}")
  public ResponseEntity<?> getBookByIsbn(@PathVariable String isbn) {
    return ResponseEntity.ok(bookService.searchBookByIsbn(isbn));
  }
}
