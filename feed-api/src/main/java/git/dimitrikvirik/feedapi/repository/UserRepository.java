package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface UserRepository extends ReactiveElasticsearchRepository<FeedUser, String> {
}
