package com.topy.bookreview.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.domain.repository.ReviewRepository;
import com.topy.bookreview.api.dto.ReviewCreateRequestDto;
import com.topy.bookreview.api.dto.ReviewCreateResponseDto;
import com.topy.bookreview.api.dto.ReviewListRequestDto;
import com.topy.bookreview.api.dto.ReviewReadResponseDto;
import com.topy.bookreview.api.dto.ReviewUpdateRequestDto;
import com.topy.bookreview.api.dto.ReviewUpdateResponseDto;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Test
  @DisplayName("❗회원이 아니면 리뷰를 저장할 수 없다.")
  void fail_saveReview_when_userNotFound() {
    // given
    String email = "username@example.com";

    ReviewCreateRequestDto request = ReviewCreateRequestDto.builder()
        .isbn("1234567890123")
        .content("내용입니다.")
        .rating(5)
        .build();

    Member member = Member.builder()
        .email(email)
        .password("1q2w3e4r!")
        .nickname("foo")
        .role(RoleType.USER)
        .build();

    Review savedReview = Review.builder()
        .id(1L)
        .build();

    given(memberRepository.findByEmail(email)).willThrow(
        new CustomException(ErrorCode.USER_NOT_FOUND));

    // then
    assertThatThrownBy(() -> reviewService.saveReview(email, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("회원을 찾을 수 없습니다.");
    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(0)).save(any(Review.class));
  }

  @Test
  @DisplayName("리뷰를 저장할 수 있다.")
  void saveReview() {
    // given
    String email = "username@example.com";

    ReviewCreateRequestDto request = ReviewCreateRequestDto.builder()
        .isbn("1234567890123")
        .content("내용입니다.")
        .rating(5)
        .build();

    Member member = Member.builder()
        .email(email)
        .password("1q2w3e4r!")
        .nickname("foo")
        .role(RoleType.USER)
        .build();

    Review savedReview = Review.builder()
        .id(1L)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.ofNullable(member));
    given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

    // when
    ReviewCreateResponseDto response = reviewService.saveReview(email, request);

    // then
    assertThat(response.getReviewId()).isNotNull();
    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).save(any(Review.class));
  }

  @Test
  @DisplayName("❗id로 리뷰를 조회 시 리뷰가 없으면 예외를 던진다.")
  void fail_findReviewById_when_ReviewNotFound() {
    // given
    Long id = 1L;

    Member author = Member.builder().build();

    Review findReview = Review.builder()
        .id(1L)
        .author(author)
        .build();

    given(reviewRepository.findById(id)).willThrow(new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    // when
    // then
    assertThatThrownBy(() -> reviewService.findReviewById(id))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("리뷰를 찾을 수 없습니다.");
    verify(reviewRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("id로 리뷰를 조회할 수 있다.")
  void findReviewById() {
    // given
    Long id = 1L;

    Member author = Member.builder().build();

    Review findReview = Review.builder()
        .id(1L)
        .author(author)
        .build();

    given(reviewRepository.findById(id)).willReturn(Optional.ofNullable(findReview));
    // when
    ReviewReadResponseDto response = reviewService.findReviewById(id);

    // then
    assertThat(response.getReviewId()).isNotNull();
    verify(reviewRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("책 isbn에 해당하는 리뷰들을 조회할 수 있다.")
  void findReviewsByIsbn() {
    // given
    String isbn = "1234567890123";
    int page = 1;
    int size = 10;
    String sort = "createdAt";

    ReviewListRequestDto request = ReviewListRequestDto.builder()
        .isbn(isbn)
        .page(page)
        .size(size)
        .sort(sort)
        .build();

    Member author = Member.builder().build();

    Review findReview = Review.builder()
        .id(1L)
        .content("내용입니다.")
        .author(author)
        .build();

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(),
        Sort.by(Direction.DESC, request.getSort()));

    Slice<Review> reviews = new SliceImpl<>(List.of(findReview), pageRequest, true);

    given(reviewRepository.findByIsbn(isbn, pageRequest)).willReturn(reviews);
    // when
    Slice<ReviewReadResponseDto> response = reviewService.findReviewsByIsbn(request);

    // then
    assertThat(response.getContent()).isNotEmpty();
    assertThat(response.getContent().get(0).getReviewId()).isEqualTo(1L);
    verify(reviewRepository, times(1)).findByIsbn(isbn, pageRequest);

  }

  @Test
  @DisplayName("❗️리뷰를 수정하는 회원이 존재하지 않는 회원이면 수정이 불가하고 예외를 던진다.")
  void fail_editReview_when_userNotFound() {

    // given
    ReviewUpdateRequestDto request = ReviewUpdateRequestDto.builder()
        .content("바뀐 내용입니다.")
        .rating(5)
        .build();

    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .content("바뀌기 전 내용입니다.")
        .author(findMember)
        .build();

    given(memberRepository.findByEmail(email)).willThrow(
        new CustomException(ErrorCode.USER_NOT_FOUND));

    // when
    // then
    assertThatThrownBy(() -> reviewService.editReview(email, reviewId, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("회원을 찾을 수 없습니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    assertThat(findReview.getContent()).isEqualTo("바뀌기 전 내용입니다.");
  }

  @Test
  @DisplayName("❗️리뷰를 수정할 때 존재하지 않는 리뷰면 수정이 불가하고 예외를 던진다.")
  void fail_editReview_when_reviewNotFound() {

    // given
    ReviewUpdateRequestDto request = ReviewUpdateRequestDto.builder()
        .content("바뀐 내용입니다.")
        .rating(5)
        .build();

    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .content("바뀌기 전 내용입니다.")
        .author(findMember)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willThrow(
        new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    // when
    // then
    assertThatThrownBy(() -> reviewService.editReview(email, reviewId, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("리뷰를 찾을 수 없습니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
    assertThat(findReview.getContent()).isEqualTo("바뀌기 전 내용입니다.");
  }

  @Test
  @DisplayName("❗️리뷰를 수정할 때 리뷰의 작성자와 로그인한 회원의 id가 다르면 수정이 불가하고 예외를 던진다.")
  void fail_editReview_when_NotAuthor() {

    // given
    ReviewUpdateRequestDto request = ReviewUpdateRequestDto.builder()
        .content("바뀐 내용입니다.")
        .rating(5)
        .build();

    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Member Author = Member.builder()
        .id(2L)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .content("바뀌기 전 내용입니다.")
        .author(Author)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(findReview));

    // when
    // then
    assertThatThrownBy(() -> reviewService.editReview(email, reviewId, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("잘못된 접근입니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
    assertThat(findReview.getContent()).isEqualTo("바뀌기 전 내용입니다.");
  }

  @Test
  @DisplayName("리뷰를 수정할 수 있다.")
  void editReview() {

    // given
    ReviewUpdateRequestDto request = ReviewUpdateRequestDto.builder()
        .content("바뀐 내용입니다.")
        .rating(5)
        .build();

    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .author(findMember)
        .content("바뀌기 전 내용입니다.")
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(findReview));

    // when
    ReviewUpdateResponseDto response = reviewService.editReview(email, reviewId, request);

    // then
    assertThat(response.getReviewId()).isNotNull();
    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
  }

  @Test
  @DisplayName("❗️리뷰를 삭제하는 회원이 존재하지 않는 회원이면 삭제가 불가하고 예외를 던진다.")
  void fail_deleteReview_when_userNotFound() {

    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .author(findMember)
        .build();

    given(memberRepository.findByEmail(email)).willThrow(
        new CustomException(ErrorCode.USER_NOT_FOUND));

    // when
    // then
    assertThatThrownBy(() -> reviewService.deleteReview(email, reviewId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("회원을 찾을 수 없습니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(0)).delete(findReview);
  }

  @Test
  @DisplayName("❗️리뷰를 삭제할 때 존재하지 않는 리뷰면 삭제가 불가하고 예외를 던진다.")
  void fail_deleteReview_when_reviewNotFound() {

    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .author(findMember)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willThrow(
        new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    // when
    // then
    assertThatThrownBy(() -> reviewService.deleteReview(email, reviewId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("리뷰를 찾을 수 없습니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewRepository, times(0)).delete(findReview);
  }

  @Test
  @DisplayName("❗️리뷰를 삭제할 때 리뷰의 작성자와 로그인한 회원의 id가 다르면 삭제가 불가하고 예외를 던진다.")
  void fail_deleteReview_when_NotAuthor() {

    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Member Author = Member.builder()
        .id(2L)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .author(Author)
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(findReview));

    // when
    // then
    assertThatThrownBy(() -> reviewService.deleteReview(email, reviewId))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("잘못된 접근입니다.");

    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewRepository, times(0)).delete(findReview);
  }

  @Test
  @DisplayName("리뷰를 삭제할 수 있다.")
  void deleteReview() {

    // given
    String email = "username@example.com";
    Long reviewId = 1L;

    Member findMember = Member.builder()
        .id(1L)
        .email(email)
        .build();

    Review findReview = Review.builder()
        .id(reviewId)
        .author(findMember)
        .content("바뀌기 전 내용입니다.")
        .build();

    given(memberRepository.findByEmail(email)).willReturn(Optional.of(findMember));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(findReview));

    // when
    reviewService.deleteReview(email, reviewId);

    // then
    verify(memberRepository, times(1)).findByEmail(email);
    verify(reviewRepository, times(1)).findById(reviewId);
  }
}