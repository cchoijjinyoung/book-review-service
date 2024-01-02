package com.topy.bookreview.client.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverBookSearchResponse {

  private String lastBuildDate;

  private Integer total;

  private Integer start;

  private Integer display;

  private List<Item> items = new ArrayList<>();


  @Getter
  @Setter
  public static class Item {

    private String title;

    private String link;

    private String image;

    private String author;

    private String discount;

    private String publisher;

    private String isbn;

    private String description;

    @JsonProperty(value = "pubdate")
    private String publishDate;

    public BookSearchResponseDto toDto() {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      LocalDate publishDate = LocalDate.parse(this.publishDate, dateTimeFormatter);
      return BookSearchResponseDto.builder()
          .image(image)
          .title(title)
          .content(description)
          .price(Integer.valueOf(discount))
          .author(author)
          .publisher(publisher)
          .publishDate(publishDate)
          .isbn(isbn)
          .build();
    }
  }
}
