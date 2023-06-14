package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import git.dimitrikvirik.feedapi.utils.UserHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService extends AbstractUserService<FeedUser, UserRepository> {


	public UserService(UserRepository repository) {
		super(repository, "user");
	}


	public Mono<FeedUser> currentUser() {
		return UserHelper.currentUserId().flatMap(this::getById);
	}

	public Flux<FeedUser> findAll(List<String> ids) {
		return repository.findAllById(ids);
	}

}
