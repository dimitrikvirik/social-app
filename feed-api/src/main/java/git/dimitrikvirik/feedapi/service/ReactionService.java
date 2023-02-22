package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactionService {

	private final ReactionRepository reactionRepository;

	public Mono<FeedReaction> createReaction(FeedReaction feedReaction) {
		return reactionRepository.save(feedReaction);
	}

	public Mono<Boolean> findByPost(String postId, String userId) {
		return reactionRepository.existsByUserIdAndPostId(userId, postId);
	}

}
