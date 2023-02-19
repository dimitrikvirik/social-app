package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.User;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface UserRepository extends ReactiveElasticsearchRepository<User, String> {
}
