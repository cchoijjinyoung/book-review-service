package com.topy.bookreview.api.domain.repository;

import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.type.RoleType;
import com.topy.bookreview.global.config.JpaAuditingConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("멤버 생성 테스트")
  void testMemberEntityCreate() {
    // given
    Member member = Member.builder()
        .email("foo@gmail.com")
        .password("bar")
        .nickname("test")
        .role(RoleType.USER)
        .build();
    // when
    Member savedMember = memberRepository.save(member);
    Member findMember = memberRepository.findById(savedMember.getId()).get();
    // then
    Assertions.assertThat(findMember.getEmail()).isEqualTo("foo@gmail.com");
    Assertions.assertThat(findMember.getPassword()).isEqualTo("bar");
    Assertions.assertThat(findMember.getNickname()).isEqualTo("test");
    Assertions.assertThat(findMember.getRole()).isEqualTo(RoleType.USER);
    Assertions.assertThat(findMember.getEmailVerifiedAt()).isNull();
  }
}