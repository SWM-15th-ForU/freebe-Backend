package com.foru.freebe.member;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;

@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;
	private Member member;

	@BeforeEach
	public void setUp() {
		member = Member.builder(1111111111L, Role.CUSTOMER, "test Member", "testMember@naver.com",
				"+82 10-0000-0000")
			.instagramId("testInstagramId")
			.build();
	}

	@Test
	public void testSaveMember() {
		//when
		Member savedMember = memberRepository.save(member);

		//then
		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getId()).isNotNull();
		assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
		assertThat(savedMember.getName()).isEqualTo(member.getName());
		assertThat(savedMember.getPhoneNumber()).isEqualTo(member.getPhoneNumber());
		assertThat(savedMember.getKakaoId()).isEqualTo(member.getKakaoId());
		assertThat(savedMember.getInstagramId()).isEqualTo(member.getInstagramId());
	}

	@Test
	public void testFindByKakaoId() {
		//given
		memberRepository.save(member);

		//when
		Optional<Member> foundMember = memberRepository.findByKakaoId(member.getKakaoId());

		// Then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getEmail()).isEqualTo(member.getEmail());
		assertThat(foundMember.get().getName()).isEqualTo(member.getName());
		assertThat(foundMember.get().getPhoneNumber()).isEqualTo(member.getPhoneNumber());
		assertThat(foundMember.get().getKakaoId()).isEqualTo(member.getKakaoId());
		assertThat(foundMember.get().getInstagramId()).isEqualTo(member.getInstagramId());
	}

}
