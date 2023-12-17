package com.topy.bookreview.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
public class EmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final ObjectMapper objectMapper;

  public EmailPasswordAuthenticationFilter(AntPathRequestMatcher antPathRequestMatcher,
      AuthenticationManager authenticationManager,
      ObjectMapper objectMapper) {
    super(antPathRequestMatcher, authenticationManager);
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

    if (!request.getMethod().equals("POST")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    if (request.getContentType() == null || !request.getContentType()
        .equals(MediaType.APPLICATION_JSON_VALUE)) {
      throw new AuthenticationServiceException(
          "Authentication content-type not supported: " + request.getContentType());
    }

    EmailPassword emailPassword = objectMapper.readValue(request.getInputStream(),
        EmailPassword.class);
    String email = emailPassword.getEmail();
    String password = emailPassword.getPassword();

    log.info("attemptAuthentication = {}", email);

    UsernamePasswordAuthenticationToken authRequest =
        UsernamePasswordAuthenticationToken.unauthenticated(email, password);

    setDetails(request, authRequest);
    return this.getAuthenticationManager().authenticate(authRequest);
  }

  protected void setDetails(HttpServletRequest request,
      UsernamePasswordAuthenticationToken authRequest) {
    authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
  }

  @Getter
  private record EmailPassword(String email, String password) {

  }
}
