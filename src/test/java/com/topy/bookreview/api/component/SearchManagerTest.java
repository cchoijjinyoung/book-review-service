package com.topy.bookreview.api.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.client.kakao.KakaoSearchClient;
import com.topy.bookreview.client.naver.NaverBookSearchRequest;
import com.topy.bookreview.client.naver.NaverBookSearchResponse;
import com.topy.bookreview.client.naver.NaverBookSearchResponse.Item;
import com.topy.bookreview.client.naver.NaverSearchClient;
import com.topy.bookreview.redis.repository.SearchHistoryRedisRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchManagerTest {

  @InjectMocks
  SearchManager searchManager;

  @Mock
  NaverSearchClient naverSearchClient;

  @Mock
  KakaoSearchClient kakaoSearchClient;

  @Mock
  SearchHistoryRedisRepository searchHistoryRedisRepository;

  @Mock
  CircuitBreakerRegistry circuitBreakerRegistry;

  @Test
  @DisplayName("CircuitBreaker 상태가 CLOSED 시 네이버 검색 API를 호출한다.")
  void searchTest_when_circuitBreaker_closed() {
    // given
    BookSearchRequestDto bookSearchRequestDto = new BookSearchRequestDto("test", 1, 1, "accuracy");
    NaverBookSearchRequest naverBookSearchRequest = NaverBookSearchRequest.of(bookSearchRequestDto);
    NaverBookSearchResponse naverBookSearchResponse = new NaverBookSearchResponse();
    List<BookSearchResponseDto> bookSearchResponseDtos = naverBookSearchResponse.getItems().stream()
        .map(Item::toDto).toList();
    // when
    when(naverSearchClient.search(naverBookSearchRequest)).thenReturn(naverBookSearchResponse);

    // then
    List<BookSearchResponseDto> results = searchManager.searchActual(bookSearchRequestDto);

    assertThat(bookSearchResponseDtos).isEqualTo(results);
    verify(naverSearchClient, times(1)).search(any(NaverBookSearchRequest.class));
  }
}