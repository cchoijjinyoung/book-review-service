package com.topy.bookreview.domain.repository;

import com.topy.bookreview.config.JpaAuditingConfig;
import com.topy.bookreview.domain.entity.Member;
import com.topy.bookreview.domain.entity.type.RoleType;
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
        .nickname("nick")
        .role(RoleType.USER)
        .build();
    // when
    Member savedMember = memberRepository.save(member);
    Member findMember = memberRepository.findById(savedMember.getId()).get();
    // then
    Assertions.assertThat(findMember.getEmail()).isEqualTo("foo@gmail.com");
    Assertions.assertThat(findMember.getPassword()).isEqualTo("bar");
    Assertions.assertThat(findMember.getNickname()).isEqualTo("nick");
    Assertions.assertThat(findMember.getRole()).isEqualTo(RoleType.USER);
    Assertions.assertThat(findMember.getEmailVerifiedDate()).isNull();
  }
}