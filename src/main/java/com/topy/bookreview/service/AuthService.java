package com.topy.bookreview.service;

import static com.topy.bookreview.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.topy.bookreview.exception.ErrorCode.ALREADY_VERIFIED_USER;
import static com.topy.bookreview.exception.ErrorCode.UNMATCHED_VERIFICATION_CODE;
import static com.topy.bookreview.exception.ErrorCode.USER_NOT_FOUND;

import com.topy.bookreview.components.mail.AuthMailForm;
import com.topy.bookreview.components.mail.MailComponents;
import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.repository.MemberRepository;
import com.topy.bookreview.dto.SignUpRequestDto;
import com.topy.bookreview.dto.SignUpResponseDto;
import com.topy.bookreview.exception.CustomException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final MailComponents mailComponents;

  private final Map<String, String> memory = new HashMap<>();

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
    mailComponents.sendMail(new AuthMailForm(savedMember.getEmail(), authCode));

    // TODO: Redis 사용
    memory.put(savedMember.getEmail(), authCode);

    return SignUpResponseDto.fromEntity(savedMember);
  }

  @Transactional
  public void verify(String email, String authCode) {
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (findMember.getEmailVerifiedAt() != null) {
      throw new CustomException(ALREADY_VERIFIED_USER);
    }

    if (authCode == null || !authCode.equals(memory.get(email))) {
      throw new CustomException(UNMATCHED_VERIFICATION_CODE);
    }
    findMember.verified();
  }
}
