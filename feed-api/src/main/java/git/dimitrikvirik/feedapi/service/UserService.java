package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import git.dimitrikvirik.feedapi.utils.UserHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService extends AbstractUserService<FeedUser, UserRepository> {


	public UserService(UserRepository repository) {
		super(repository, "user");
	}


	public Mono<FeedUser> currentUser() {
		return UserHelper.currentUserId().flatMap(this::getById);
	}

}
