package com.topy.bookreview.global.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CustomCircuitBreakerConfig {

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  public CircuitBreaker bookSearchCircuitBreaker() {
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofMillis(5000))
        .permittedNumberOfCallsInHalfOpenState(2)
        .slidingWindowSize(6)
        .build();

    return circuitBreakerRegistry.circuitBreaker("BOOK_SEARCH", circuitBreakerConfig);
  }

  @Bean
  public CircuitBreaker bookSearchDetailCircuitBreaker() {
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofMillis(5000))
        .permittedNumberOfCallsInHalfOpenState(2)
        .slidingWindowSize(6)
        .build();

    return circuitBreakerRegistry.circuitBreaker("BOOK_SEARCH_DETAIL", circuitBreakerConfig);
  }
}
