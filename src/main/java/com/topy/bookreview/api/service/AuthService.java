package com.topy.bookreview.api.service;


import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EMAIL_VERIFIED_USER;
import static com.topy.bookreview.global.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.topy.bookreview.global.exception.ErrorCode.EXPIRED_AUTH_CODE;
import static com.topy.bookreview.global.exception.ErrorCode.UNMATCHED_AUTH_CODE;
import static com.topy.bookreview.global.exception.ErrorCode.USER_NOT_FOUND;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.api.dto.SignUpRequestDto;
import com.topy.bookreview.api.dto.SignUpResponseDto;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.manager.mail.AuthMailForm;
import com.topy.bookreview.global.manager.mail.MailSenderManager;
import com.topy.bookreview.redis.repository.AuthCodeRedisRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final MailSenderManager mailSenderManager;

  private final AuthCodeRedisRepository authCodeRedisRepository;

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
    mailSenderManager.sendMail(new AuthMailForm(savedMember.getEmail(), authCode));
    authCodeRedisRepository.saveByEmail(savedMember.getEmail(), authCode);

    return SignUpResponseDto.fromEntity(savedMember);
  }

  @Transactional
  public void mailVerify(String email, String authCode, long requestTimeMillis) {
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (findMember.getEmailVerifiedAt() != null) {
      throw new CustomException(ALREADY_EMAIL_VERIFIED_USER);
    }

    String storedAuthCode = (String) authCodeRedisRepository.getByEmail(email);
    Long expiredTimeMillis = authCodeRedisRepository.getExpireByEmail(email);

    if (ObjectUtils.isEmpty(storedAuthCode) || expiredTimeMillis == null
        || requestTimeMillis > expiredTimeMillis) {
      throw new CustomException(EXPIRED_AUTH_CODE);
    }

    if (authCode == null || !authCode.equals(storedAuthCode)) {
      throw new CustomException(UNMATCHED_AUTH_CODE);
    }
    findMember.verified();
  }
}
