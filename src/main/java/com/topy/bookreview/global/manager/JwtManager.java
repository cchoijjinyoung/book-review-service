package com.topy.bookreview.global.manager;

import static com.topy.bookreview.global.type.ExpiryTime.ACCESS_TOKEN;
import static com.topy.bookreview.global.type.ExpiryTime.REFRESH_TOKEN;

import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import com.topy.bookreview.security.CustomUserDetails;
import com.topy.bookreview.security.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class JwtManager {

  private final CustomUserDetailsServiceImpl userDetailsService;

  @Value("${jwt.key}")
  private String key;

  private SecretKey secretKey;

  private JwtParser parser;

  private static final long ACCESS_TOKEN_EXPIRY_MILLIS = ACCESS_TOKEN.getExpiryTimeMillis();
  private static final long REFRESH_TOKEN_EXPIRY_MILLIS = REFRESH_TOKEN.getExpiryTimeMillis();

  private static final String ROLE_KEY = "roles";

  @PostConstruct
  private void initSecretKey() {
    secretKey = Keys.hmacShaKeyFor(key.getBytes());
    parser = Jwts.parser().verifyWith(secretKey).build();
  }

  public String generateAccessToken(Authentication authentication) {
    return generateToken(authentication, ACCESS_TOKEN_EXPIRY_MILLIS);
  }

  public String generateRefreshToken(Authentication authentication) {
    return generateToken(authentication, REFRESH_TOKEN_EXPIRY_MILLIS);
  }

  public Authentication createAuthentication(String token) {
    Claims claims = parseClaims(token);
    String subject = claims.getSubject();
    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
        subject);
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
  }

  public boolean isExpiredToken(String token) {
    Claims claims = parseClaims(token);

    Date expirationDate = claims.getExpiration();
    return expirationDate.before(new Date());
  }

  private String generateToken(Authentication authentication, long validityMillis) {
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + validityMillis);

    return Jwts.builder()
        .subject(authentication.getName())
        .signWith(secretKey, SIG.HS256)
        .expiration(expiredDate)
        .issuedAt(now)
        .compact();
  }

  private Claims parseClaims(String token) {
    try {
      return parser.parseSignedClaims(token).getPayload();
    } catch (Exception e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }
}


