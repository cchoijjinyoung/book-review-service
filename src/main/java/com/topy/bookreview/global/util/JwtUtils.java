package com.topy.bookreview.global.util;

import com.topy.bookreview.security.CustomUserDetails;
import com.topy.bookreview.security.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class JwtUtils {

  private final CustomUserDetailsServiceImpl userDetailsService;

  @Value("${jwt.key}")
  private String key;

  private SecretKey secretKey;

  private JwtParser parser;

  private static final long ACCESS_TOKEN_EXPIRED_MILLIS = 1000 * 60 * 60;
  private static final long REFRESH_TOKEN_EXPIRED_MILLIS = 1000 * 60 * 60 * 24 * 14;

  private static final String ROLE_KEY = "roles";

  @PostConstruct
  private void setSecretKey() {
    secretKey = Keys.hmacShaKeyFor(key.getBytes());
    parser = Jwts.parser().verifyWith(secretKey).build();
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

    return Jwts.builder()
        .subject(authentication.getName())
        .signWith(secretKey, SIG.HS256)
        .expiration(expiredDate)
        .issuedAt(now)
        .compact();
  }

  public boolean isExpiredToken(String token) {
    try {
      Claims claims = parseClaims(token);

      Date expirationDate = claims.getExpiration();
      return expirationDate.before(new Date());
    } catch (Exception e) {
      return true;
    }
  }

  public Authentication createAuthentication(String token) {
    Claims claims = parseClaims(token);
    String subject = getSubject(claims);
    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
        subject);
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
  }

  private Claims parseClaims(String token) {
    return parser.parseSignedClaims(token).getPayload();
  }

  private String getSubject(Claims claims) {
    return claims.getSubject();
  }
}


