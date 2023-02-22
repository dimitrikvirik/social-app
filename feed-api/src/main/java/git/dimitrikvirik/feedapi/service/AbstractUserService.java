package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.UserResource;
import git.dimitrikvirik.feedapi.utils.UserHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public abstract class AbstractUserService<T extends UserResource, R extends ReactiveElasticsearchRepository<T, String>> extends AbstractService<T, R> {

	public AbstractUserService(R repository, String indexName) {
		super(repository, indexName);
	}

	@NotNull
	public Mono<T> getByIdValidated(String id) {
		return getById(id).zipWith(UserHelper.currentUserId())
				.flatMap(tuple -> {
					T entity = tuple.getT1();
					String userId = tuple.getT2();
					if (!entity.getUserId().equals(userId)) {
						return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't update/delete other user's resource"));
					}
					return Mono.just(entity);
				});

	}
}
