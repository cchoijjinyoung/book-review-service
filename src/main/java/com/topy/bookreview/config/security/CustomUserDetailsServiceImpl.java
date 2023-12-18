package com.topy.bookreview.config.security;

import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.repository.MemberRepository;
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
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));

    CustomUserDetails customUserDetails = CustomUserDetails.of(findMember);

    log.info("customUserDetails.getRole() = {}", customUserDetails.getRole());

    return customUserDetails;
  }
}