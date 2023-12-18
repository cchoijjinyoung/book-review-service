package com.topy.bookreview.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.config.security.filter.EmailPasswordAuthenticationFilter;
import com.topy.bookreview.config.security.handler.LoginFailureHandler;
import com.topy.bookreview.config.security.handler.LoginSuccessHandler;
import com.topy.bookreview.domain.repository.MemberRepository;
import com.topy.bookreview.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  private final TokenProvider tokenProvider;
  private final ObjectMapper objectMapper;
  private final MemberRepository memberRepository;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers("/favicon.ico")
        .requestMatchers("/error");
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable);

    http
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll());

    http.addFilterBefore(emailPasswordAuthenticationFilter(),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter() {
    EmailPasswordAuthenticationFilter filter = new EmailPasswordAuthenticationFilter(
        new AntPathRequestMatcher("/auth/signin", "POST"),
        authenticationManager(), objectMapper);

    filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(tokenProvider, objectMapper));
    filter.setAuthenticationFailureHandler(new LoginFailureHandler(objectMapper));
    return filter;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(provider);
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new CustomUserDetailsServiceImpl(memberRepository);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
