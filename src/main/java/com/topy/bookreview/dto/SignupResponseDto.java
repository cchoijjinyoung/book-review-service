package com.topy.bookreview.dto;

import com.topy.bookreview.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {

  private String nickname;

  public static SignupResponseDto fromEntity(Member member) {
    return SignupResponseDto.builder()
        .nickname(member.getNickname())
        .build();
  }
}
