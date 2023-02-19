package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.User;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;


	@PostConstruct
	public void test() {
		userRepository.save(new User("1", "Dimitri", "Kvirikashvili", "https://avatars.githubusercontent.com/u/56852436?v=4")).subscribe();
	}

	public Mono<User> userById(String id) {
		return userRepository.findById(id);
	}

}
