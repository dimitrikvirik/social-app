package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.User;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;


	public Mono<User> userById(String id) {
		return userRepository.findById(id);
	}

	public Disposable save(User user) {
		return userRepository.save(user).subscribe();
	}

}
