package com.topy.bookreview.api.service;


import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EMAIL_VERIFIED_USER;
import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.topy.bookreview.global.exception.ErrorCode.UNMATCHED_VERIFICATION_CODE;
import static com.topy.bookreview.global.exception.ErrorCode.USER_NOT_FOUND;

import com.topy.bookreview.global.exception.ErrorCode;
import com.topy.bookreview.global.util.JwtUtils;
import com.topy.bookreview.global.util.mail.AuthMailForm;
import com.topy.bookreview.global.util.mail.MailUtils;
import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.dto.SignUpResponseDto;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.redis.RedisUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final MailUtils mailUtils;

  private final RedisUtils redisUtils;

  private final JwtUtils jwtUtils;

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    if (memberRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
      throw new CustomException(ALREADY_EXISTS_EMAIL);
    }

    String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

    Member savedMember = memberRepository.save(signUpRequestDto.toEntity(encodedPassword));

    String authCode = UUID.randomUUID().toString();
    mailUtils.sendMail(new AuthMailForm(savedMember.getEmail(), authCode));
    redisUtils.save(savedMember.getEmail(), authCode);

    return SignUpResponseDto.fromEntity(savedMember);
  }

  @Transactional
  public void mailVerify(String email, String authCode) {
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (findMember.getEmailVerifiedAt() != null) {
      throw new CustomException(ALREADY_EMAIL_VERIFIED_USER);
    }

    String storedAuthCode = (String) redisUtils.get(email);

    if (authCode == null || !authCode.equals(storedAuthCode)) {
      throw new CustomException(UNMATCHED_VERIFICATION_CODE);
    }
    findMember.verified();
  }

  public String reissueAccessToken(String refreshToken) {
    String storedToken = (String) redisUtils.get(refreshToken);
    if (jwtUtils.isExpiredToken(storedToken)) {
      throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    Authentication authentication = jwtUtils.createAuthentication(refreshToken);
    return jwtUtils.generateAccessToken(authentication);
  }
}
