package com.topy.bookreview.dto;

import com.topy.bookreview.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponseDto {

  private String nickname;

  public static SignUpResponseDto fromEntity(Member member) {
    return SignUpResponseDto.builder()
        .nickname(member.getNickname())
        .build();
  }
}
