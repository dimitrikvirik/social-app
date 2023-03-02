package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;

public interface ReactionRepository extends ReactiveElasticsearchRepository<FeedReaction, String> {


	Mono<Boolean> existsByFeedUserIdAndPostId(String userId, String postId);



	Mono<Void> deleteAllByPostId(String postId);
}
