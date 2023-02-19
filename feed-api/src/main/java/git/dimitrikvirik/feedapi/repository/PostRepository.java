package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface PostRepository extends ReactiveElasticsearchRepository<FeedPost, String> {
}
