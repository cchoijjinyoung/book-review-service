package com.topy.bookreview.client.kakao;

import com.topy.bookreview.api.dto.BookSearchResponseDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoBookSearchResponse {

  private Meta meta;

  private List<Document> documents = new ArrayList<>();

  @Getter
  @Setter
  public static class Meta {

    private boolean is_end;
    private int pageable_count;
    private int total_count;
  }

  @Getter
  @Setter
  public static class Document {

    private List<String> authors = new ArrayList<>();
    private String contents;
    private String datetime;
    private String isbn;
    private int price;
    private String publisher;
    private int sale_price;
    private String status;
    private String thumbnail;
    private String title;
    private List<String> translators = new ArrayList<>();
    private String url;

    public BookSearchResponseDto toDto() {
      return BookSearchResponseDto.builder()
          .image(thumbnail)
          .title(title)
          .content(contents)
          .price(price)
          .author(authors.isEmpty() ? null : authors.get(0))
          .publisher(publisher)
          .publishDate(parseDateTime(datetime))
          .isbn(isbn.length() > 13 ? isbn.split(" ")[1] : isbn)
          .build();
    }

    private LocalDate parseDateTime(String datetime) {
      ZonedDateTime zonedDateTime = ZonedDateTime.parse(datetime,
          DateTimeFormatter.ISO_OFFSET_DATE_TIME);
      return zonedDateTime.toLocalDate();
    }
  }
}
