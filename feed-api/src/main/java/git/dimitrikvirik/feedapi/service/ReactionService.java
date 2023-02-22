package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.repository.ReactionRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ReactionService {

	private final ReactionRepository reactionRepository;

	private final ReactiveElasticsearchOperations operations;

	public Mono<FeedReaction> createReaction(FeedReaction feedReaction) {
		return reactionRepository.save(feedReaction);
	}

	public Mono<Boolean> findByPost(String postId, String userId) {
		return reactionRepository.existsByUserIdAndPostId(userId, postId);
	}

	public Flux<FeedReaction> getAllReactions(String userId, Integer page, Integer size) {
		return ElasticsearchBuilder.create(
				FeedReaction.class,
				PageRequest.of(page, size),
				operations
		).modifyQuery(builder ->
				builder.must(mustBuilder -> mustBuilder.term(TermQuery.of(
										termQueryBuilder -> termQueryBuilder.field("userId.keyword").value(userId)
								)
						)
				)
		).doSearch();
	}

	public Mono<Void> delete(FeedReaction feedReaction) {
		return reactionRepository.delete(feedReaction);
	}

	public Mono<FeedReaction> getById(String id) {
		return reactionRepository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found")));
	}
}
