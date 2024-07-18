package com.foru.freebe.user;

import static com.foru.freebe.user.entity.Role.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.foru.freebe.user.entity.User;
import com.foru.freebe.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	private User user;

	@BeforeEach
	public void setUp() {
		user = User.builder(1111111111L, ROLE_USER, "test user", "testUser@naver.com", "+82 10-0000-0000")
			.build();
	}

	@Test
	public void testSaveUser() {
		//when
		User savedUser = userRepository.save(user);

		//then
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isNotNull();
		assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
		assertThat(savedUser.getName()).isEqualTo(user.getName());
		assertThat(savedUser.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
		assertThat(savedUser.getKakaoId()).isEqualTo(user.getKakaoId());
	}

	@Test
	public void testFindByKakaoId() {
		//given
		userRepository.save(user);

		//when
		Optional<User> foundUser = userRepository.findByKakaoId(user.getKakaoId());

		// Then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
		assertThat(foundUser.get().getName()).isEqualTo(user.getName());
		assertThat(foundUser.get().getPhoneNumber()).isEqualTo(user.getPhoneNumber());
		assertThat(foundUser.get().getKakaoId()).isEqualTo(user.getKakaoId());
	}

}
