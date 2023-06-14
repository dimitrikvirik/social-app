package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface FeedFriendsRepository extends ReactiveElasticsearchRepository<FeedFriends, String> {
	Mono<FeedFriends> findByIdAndStatusIn(String id, List<FriendshipStatus> statuses);
}
