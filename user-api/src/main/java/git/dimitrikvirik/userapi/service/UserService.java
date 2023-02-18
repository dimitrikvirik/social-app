package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;

	public User save(User user) {
		return userRepository.save(user);
	}

	public void deleteWaitingUsers() {
		userRepository.deleteByIsDisabledAndUpdatedAtAfter(true, LocalDateTime.now().minusMonths(1));
	}

	public void delete(String id) {
		userRepository.deleteById(id);
	}

	public User findById(String id) {
		return userRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> findByKeycloakKId(String keycloakId) {
		return userRepository.findByKeycloakId(keycloakId);
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

}
