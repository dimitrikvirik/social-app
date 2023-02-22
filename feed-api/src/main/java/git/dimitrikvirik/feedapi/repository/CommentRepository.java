package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedComment;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;

public interface CommentRepository extends ReactiveElasticsearchRepository<FeedComment, String> {

	Mono<Void> deleteByPostId(String postId);
}
