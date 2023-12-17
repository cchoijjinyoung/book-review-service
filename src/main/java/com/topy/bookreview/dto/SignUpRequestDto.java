package com.topy.bookreview.dto;

import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.entity.type.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String password;

  @NotBlank
  @Size(max = 8)
  private String nickname;

  public Member toEntity(String encodedPassword) {
    return Member.builder()
        .email(email)
        .password(encodedPassword)
        .nickname(nickname)
        .role(RoleType.USER)
        .build();
  }
}
