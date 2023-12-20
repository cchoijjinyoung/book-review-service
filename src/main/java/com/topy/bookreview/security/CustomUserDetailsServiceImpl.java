package com.topy.bookreview.security;

import static com.topy.bookreview.global.exception.ErrorCode.*;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("loadUserByUsername = {}", username);

    Member findMember = memberRepository.findByEmail(username)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    CustomUserDetails customUserDetails = new CustomUserDetails(findMember);

    log.info("customUserDetails.getRole() = {}", customUserDetails.getAuthorities());

    return customUserDetails;
  }
}
