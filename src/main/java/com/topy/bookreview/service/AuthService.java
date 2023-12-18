package com.topy.bookreview.service;

import com.topy.bookreview.components.mail.AuthMailForm;
import com.topy.bookreview.components.mail.MailComponents;
import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.repository.MemberRepository;
import com.topy.bookreview.dto.SignUpRequestDto;
import com.topy.bookreview.dto.SignupResponseDto;
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
  public SignupResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    if (memberRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
      throw new RuntimeException("이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

    Member savedMember = memberRepository.save(signUpRequestDto.toEntity(encodedPassword));

    String authCode = UUID.randomUUID().toString();
    mailComponents.sendMail(new AuthMailForm(savedMember.getEmail(), authCode));

    // TODO: Redis 사용
    memory.put(savedMember.getEmail(), authCode);

    return SignupResponseDto.fromEntity(savedMember);
  }

  @Transactional
  public void verify(String email, String authCode) {
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

    if (findMember.getEmailVerifiedDate() != null) {
      throw new RuntimeException("이미 인증된 회원입니다.");
    }

    if (authCode == null || !authCode.equals(memory.get(email))) {
      throw new RuntimeException("인증 코드가 올바르지 않습니다.");
    }
    findMember.verified();
    log.info("이메일 인증성공 ={}", findMember.getEmail());
  }
}
