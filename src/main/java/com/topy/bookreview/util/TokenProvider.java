package com.topy.bookreview.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TokenProvider {

  @Value("${jwt.key}")
  private String key;

  private SecretKey secretKey;

  private static final long ACCESS_TOKEN_EXPIRED_MILLIS = 1000 * 60 * 60;
  private static final long REFRESH_TOKEN_EXPIRED_MILLIS = 1000 * 60 * 60 * 24 * 14;

  private static final String ROLE_KEY = "role";

  @PostConstruct
  private void setSecretKey() {
    secretKey = Keys.hmacShaKeyFor(key.getBytes());
  }

  public String generateAccessToken(Authentication authentication) {
    return generateToken(authentication, ACCESS_TOKEN_EXPIRED_MILLIS);
  }

  public String generateRefreshToken(Authentication authentication) {
    return generateToken(authentication, REFRESH_TOKEN_EXPIRED_MILLIS);
  }

  public String generateToken(Authentication authentication, long expiredMillis) {
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + expiredMillis);

    String accessToken = Jwts.builder()
        .subject(authentication.getName())
        .claim(ROLE_KEY, authentication.getAuthorities())
        .signWith(secretKey, SIG.HS256)
        .expiration(expiredDate)
        .issuedAt(now)
        .compact();

    return accessToken;
  }
}
