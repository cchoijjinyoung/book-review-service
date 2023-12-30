package com.topy.bookreview.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.api.service.BookService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

  @MockBean
  private BookService bookService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("책 검색 테스트")
  void bookSearchTest() throws Exception {
    // given
    BookSearchResponseDto bookSearchResponseDto = BookSearchResponseDto.builder()
        .image("이미지1")
        .title("제목1")
        .content("내용1")
        .author("작가1")
        .publisher("출판사1")
        .publishDate(LocalDate.now())
        .price(1000)
        .isbn("1234567890123")
        .build();

    String keyword = "test";
    Integer page = 1;
    Integer size = 10;
    String sort = "accuracy";

    Mockito.when(bookService.search(any(BookSearchRequestDto.class)))
        .thenReturn(List.of(bookSearchResponseDto));

    mockMvc.perform(MockMvcRequestBuilders.get("/book/search")
            .param("keyword", keyword)
            .param("page", page.toString())
            .param("size", size.toString())
            .param("sort", sort)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    Mockito.verify(bookService, Mockito.times(1)).search(any(BookSearchRequestDto.class));
  }
}