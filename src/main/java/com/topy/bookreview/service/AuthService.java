package com.topy.bookreview.service;

import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.repository.MemberRepository;
import com.topy.bookreview.dto.SignUpRequestDto;
import com.topy.bookreview.dto.SignupResponseDto;
import com.topy.bookreview.util.TokenProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthMailService authMailService;

  private final Map<String, String> memory = new HashMap<>();

  private final MemberRepository memberRepository;

  private final TokenProvider tokenProvider;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignupResponseDto signUp(SignUpRequestDto signUpRequestDto) {
    if (memberRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
      throw new RuntimeException("이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

    Member savedMember = memberRepository.save(signUpRequestDto.toEntity(encodedPassword));

    String authCode = UUID.randomUUID().toString();

    authMailService.sendToEmail(authCode, savedMember.getEmail());

    // TODO: Redis 사용
    memory.put(savedMember.getEmail(), authCode);

    return SignupResponseDto.fromEntity(savedMember);
  }

  @Transactional
  public void verify(String email, String authCode) {
    if (!authCode.equals(memory.get(email))) {
      throw new RuntimeException("인증 코드가 올바르지 않습니다.");
    }
    Member findMember = memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

    findMember.verified();
  }
}
