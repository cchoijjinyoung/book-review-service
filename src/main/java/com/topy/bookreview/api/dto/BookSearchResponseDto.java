package com.topy.bookreview.api.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSearchResponseDto {

  private String image;

  private String title;

  private String author;

  private Integer price;

  private String publisher;

  private String isbn;

  private String content;

  private LocalDate publishDate;
}
