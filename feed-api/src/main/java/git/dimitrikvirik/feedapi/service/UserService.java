package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import git.dimitrikvirik.feedapi.utils.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;


	public Mono<FeedUser> userById(String id) {
		return userRepository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.GONE, "User not found")));
	}

	public Mono<FeedUser> save(FeedUser feedUser) {
		return userRepository.save(feedUser);
	}

	public Mono<FeedUser> currentUser() {
		return UserHelper.currentUserId().flatMap(this::userById);
	}

}
