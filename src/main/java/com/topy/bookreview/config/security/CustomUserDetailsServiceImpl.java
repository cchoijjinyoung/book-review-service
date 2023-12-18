package com.topy.bookreview.config.security;

import static com.topy.bookreview.exception.ErrorCode.*;

import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.repository.MemberRepository;
import com.topy.bookreview.exception.CustomException;
import com.topy.bookreview.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername = {}", username);

    Member findMember = memberRepository.findByEmail(username)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    CustomUserDetails customUserDetails = CustomUserDetails.of(findMember);

    log.info("customUserDetails.getRole() = {}", customUserDetails.getRole());

    return customUserDetails;
  }
}
