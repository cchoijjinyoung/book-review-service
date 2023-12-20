package com.topy.bookreview.service;

import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EMAIL_VERIFIED_USER;
import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.topy.bookreview.global.exception.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.topy.bookreview.global.exception.ErrorCode.UNMATCHED_VERIFICATION_CODE;
import static com.topy.bookreview.global.exception.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.dto.SignUpResponseDto;
import com.topy.bookreview.api.service.AuthService;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.util.JwtUtils;
import com.topy.bookreview.global.util.mail.AuthMailForm;
import com.topy.bookreview.global.util.mail.MailUtils;
import com.topy.bookreview.redis.RedisUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  MemberRepository memberRepository;

  @Mock
  MailUtils mailUtils;

  @Mock
  RedisUtils redisUtils;

  @Mock
  JwtUtils jwtUtils;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  AuthService authService;

  @Test
  @DisplayName("❗이미 존재하는 이메일이면 회원가입은 실패한다.")
  void signUpTest() {
    // given
    Member member = verifiedMember();

    SignUpRequestDto signUpRequestDto = new SignUpRequestDto("email", "password",
        "nickname");

    // when
    when(memberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(member));

    // then
    assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
        .isInstanceOf(CustomException.class)
        .hasMessage(ALREADY_EXISTS_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원가입이 완료되면 nickname을 응답한다.")
  void passwordEncodingTest() {

    // given
    SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
        "foo@gmail.com", "1q2w3e4r", "bar");
    when(memberRepository.findByEmail(signUpRequestDto.getEmail())).thenReturn(Optional.empty());

    String encodedPassword = "encodedPassword";
    when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn(encodedPassword);

    Member savedMember = verifiedMember();
    when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

    doNothing().when(mailUtils).sendMail(any(AuthMailForm.class));
    doNothing().when(redisUtils).save(any(String.class), any(String.class));

    // when
    SignUpResponseDto signUpResponseDto = authService
        .signUp(signUpRequestDto);

    // then
    assertThat(signUpResponseDto).isNotNull();
    assertThat(signUpResponseDto.getNickname()).isEqualTo("bar");

    verify(mailUtils).sendMail(any(AuthMailForm.class));
    verify(redisUtils).save(any(String.class), any(String.class));
  }

  @Test
  @DisplayName("❗이메일 요청이 들어온 회원이 존재하지 않는 회원일 시 검증은 실패한다.")
  void testVerifyUserNotFound() {
    // given
    String email = "foo@gmail.com";
    String authCode = "123456";
    when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());
    // when
    // then
    assertThatThrownBy(() -> authService.mailVerify(email, authCode))
        .isInstanceOf(CustomException.class)
        .hasMessage(USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("❗이미 이메일 인증이 된 회원은 이메일 검증 시 실패한다.")
  void testVerifyAlreadyEmailVerified() {
    // given
    String email = "foo@gmail.com";
    String authCode = "123456";

    Member member = verifiedMember();
    when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
    // when
    // then
    assertThatThrownBy(() -> authService.mailVerify(email, authCode))
        .isInstanceOf(CustomException.class)
        .hasMessage(ALREADY_EMAIL_VERIFIED_USER.getMessage());
  }

  @Test
  @DisplayName("❗인증코드가 불일치할 시 검증은 실패한다.")
  void testVerifyUnmatchedVerificationCode() {
    // given
    String email = "foo@gmail.com";
    String authCode = "123456";
    String storedAuthCode = "654321";

    Member member = unVerifiedMember();
    when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));

    when(redisUtils.get(email)).thenReturn(storedAuthCode);
    // when
    // then
    assertThatThrownBy(() -> authService.mailVerify(email, authCode))
        .isInstanceOf(CustomException.class)
        .hasMessage(UNMATCHED_VERIFICATION_CODE.getMessage());
  }

  @Test
  @DisplayName("회원가입에 성공하면 회원의 인증날짜컬럼에 값이 들어간다.")
  void successfulVerify() {
    // given
    String email = "foo@gmail.com";
    String authCode = "123456";
    String storedAuthCode = "123456";
    Member member = unVerifiedMember();
    // when
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(redisUtils.get(email)).thenReturn(storedAuthCode);
    // then
    authService.mailVerify(email, authCode);
    assertThat(member.getEmailVerifiedAt()).isNotNull();
  }

  @Test
  @DisplayName("❗리프래시 토큰이 만료됐으면 실패한다.")
  void reissueAccessToken_fail_tokenExpired() {
    // given
    String refreshToken = "12345";
    String storedToken = "12345";

    // when
    when(redisUtils.get(refreshToken)).thenReturn(storedToken);
    when(jwtUtils.isExpiredToken(storedToken)).thenReturn(true);

    // then
    assertThatThrownBy(() -> authService.reissueAccessToken(refreshToken))
        .isInstanceOf(CustomException.class)
        .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
  }

  @Test
  @DisplayName("액세스토큰 재발급에 성공하면 액세스토큰을 응답한다.")
  void reissueAccessToken_success() {
    // given
    String refreshToken = "12345";
    String storedToken = "12345";
    String accessToken = "54321";

    // when
    when(redisUtils.get(refreshToken)).thenReturn(storedToken);
    when(jwtUtils.isExpiredToken(storedToken)).thenReturn(false);

    Authentication authentication = new TestingAuthenticationToken("test", "test");
    when(jwtUtils.createAuthentication(refreshToken)).thenReturn(authentication);
    when(jwtUtils.generateAccessToken(authentication)).thenReturn(accessToken);

    String result = authService.reissueAccessToken(refreshToken);

    assertThat(accessToken).isEqualTo(result);
  }

  private Member verifiedMember() {
    return Member.builder()
        .email("foo@gmail.com")
        .password("1q2w3e4r")
        .nickname("bar")
        .role(RoleType.USER)
        .emailVerifiedAt(LocalDateTime.now())
        .build();
  }

  private Member unVerifiedMember() {
    return Member.builder()
        .email("foo@gmail.com")
        .password("1q2w3e4r")
        .nickname("bar")
        .role(RoleType.USER)
        .build();
  }
}