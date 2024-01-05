package com.topy.bookreview.api.component;

import com.topy.bookreview.api.dto.BookSearchRequestDto;
import com.topy.bookreview.api.dto.BookSearchResponseDto;
import com.topy.bookreview.client.kakao.KakaoBookSearchRequest;
import com.topy.bookreview.client.kakao.KakaoBookSearchResponse;
import com.topy.bookreview.client.kakao.KakaoBookSearchResponse.Document;
import com.topy.bookreview.client.kakao.KakaoSearchClient;
import com.topy.bookreview.client.naver.NaverBookSearchRequest;
import com.topy.bookreview.client.naver.NaverBookSearchResponse;
import com.topy.bookreview.client.naver.NaverBookSearchResponse.Item;
import com.topy.bookreview.client.naver.NaverSearchClient;
import com.topy.bookreview.redis.repository.SearchDetailHistoryRedisRepository;
import com.topy.bookreview.redis.repository.SearchHistoryRedisRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchManager {

  private final NaverSearchClient naverSearchClient;
  private final KakaoSearchClient kakaoSearchClient;
  private final SearchHistoryRedisRepository searchHistoryRedisRepository;
  private final SearchDetailHistoryRedisRepository searchDetailHistoryRedisRepository;

  @CircuitBreaker(name = "BOOK_SEARCH", fallbackMethod = "searchCache")
  public List<BookSearchResponseDto> searchActual(BookSearchRequestDto bookSearchRequestDto) {
    log.info("네이버 검색 API 호출");
    NaverBookSearchRequest naverBookSearchRequest = NaverBookSearchRequest.of(bookSearchRequestDto);
    NaverBookSearchResponse naverBookSearchResponse = naverSearchClient.search(
        naverBookSearchRequest);

    return naverBookSearchResponse.getItems().stream().map(Item::toDto).toList();
  }

  public List<BookSearchResponseDto> searchCache(BookSearchRequestDto bookSearchRequestDto,
      Throwable throwable) {
    log.info("서킷브레이커 OPEN - 캐시저장소 호출", throwable);
    List<BookSearchResponseDto> result = searchHistoryRedisRepository.get(bookSearchRequestDto);
    if (result == null) {
      log.info("검색 조건에 맞는 캐시가 존재히지 않습니다.");
      return searchFinal(bookSearchRequestDto);
    }
    return result;
  }

  public List<BookSearchResponseDto> searchFinal(BookSearchRequestDto bookSearchRequestDto) {
    log.info("카카오 검색 API 호출");
    KakaoBookSearchRequest kakaoBookSearchRequest = KakaoBookSearchRequest.of(bookSearchRequestDto);
    KakaoBookSearchResponse kakaoBookSearchResponse = kakaoSearchClient.search(
        kakaoBookSearchRequest);
    return kakaoBookSearchResponse.getDocuments().stream().map(Document::toDto).toList();
  }

  @CircuitBreaker(name = "BOOK_SEARCH_DETAIL", fallbackMethod = "searchDetailCache")
  public BookSearchResponseDto searchDetailActual(String isbn) {
    NaverBookSearchResponse naverBookSearchResponse = naverSearchClient.searchDetail(isbn);
    return naverBookSearchResponse.getItems().get(0).toDto();
  }

  public BookSearchResponseDto searchDetailCache(String isbn, Throwable throwable) {
    log.info("서킷브레이커 OPEN - 캐시저장소 호출", throwable);
    BookSearchResponseDto result = searchDetailHistoryRedisRepository.get(isbn);
    if (result == null) {
      log.info("검색 조건에 맞는 캐시가 존재히지 않습니다.");
      return searchDetailFinal(isbn);
    }
    return result;
  }

  public BookSearchResponseDto searchDetailFinal(String isbn) {
    log.info("카카오 검색 API 호출");
    KakaoBookSearchResponse kakaoBookSearchResponse = kakaoSearchClient.searchDetail(isbn);
    return kakaoBookSearchResponse.getDocuments().get(0).toDto();
  }
}
