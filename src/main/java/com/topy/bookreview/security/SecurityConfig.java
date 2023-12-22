package com.topy.bookreview.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topy.bookreview.api.domain.repository.MemberRepository;
import com.topy.bookreview.global.manager.JwtManager;
import com.topy.bookreview.redis.RedisManager;
import com.topy.bookreview.security.filter.EmailPasswordAuthenticationFilter;
import com.topy.bookreview.security.filter.JwtAuthenticationFilter;
import com.topy.bookreview.security.handler.CustomLogoutHandler;
import com.topy.bookreview.security.handler.CustomLogoutSuccessHandler;
import com.topy.bookreview.security.handler.ForbiddenHandler;
import com.topy.bookreview.security.handler.LoginFailureHandler;
import com.topy.bookreview.security.handler.LoginSuccessHandler;
import com.topy.bookreview.security.handler.UnAuthorizedHandler;
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

  private final JwtManager jwtManager;
  private final RedisManager redisManager;
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
    return http
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .logout(c -> c
            .logoutUrl("/auth/signout")
            .logoutSuccessUrl("/login")
            .addLogoutHandler(new CustomLogoutHandler(redisManager))
            .logoutSuccessHandler(new CustomLogoutSuccessHandler()))
        .exceptionHandling(e -> {
              e.accessDeniedHandler(new ForbiddenHandler(objectMapper));
              e.authenticationEntryPoint(new UnAuthorizedHandler(objectMapper));
            }
        )
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                new AntPathRequestMatcher("/"),
                new AntPathRequestMatcher("/logout"),
                new AntPathRequestMatcher("/auth/logout"),
                new AntPathRequestMatcher("/auth/signup"),
                new AntPathRequestMatcher("/auth/signin")
            ).permitAll()

            .anyRequest().authenticated())
        .addFilterBefore(emailPasswordAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter() {
    EmailPasswordAuthenticationFilter filter = new EmailPasswordAuthenticationFilter(
        new AntPathRequestMatcher("/auth/signin", "POST"),
        authenticationManager(), objectMapper);

    filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(jwtManager, redisManager));
    filter.setAuthenticationFailureHandler(new LoginFailureHandler(objectMapper));
    return filter;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtManager, redisManager);
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
