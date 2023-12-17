package com.topy.bookreview.dto;

import com.topy.bookreview.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDto {

  private String email;

  private String password;

  public static SignInResponseDto fromEntity(Member member) {
    return SignInResponseDto.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .build();
  }
}
