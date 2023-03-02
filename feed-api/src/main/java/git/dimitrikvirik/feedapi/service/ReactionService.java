package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.repository.ReactionRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReactionService extends AbstractUserService<FeedReaction, ReactionRepository> {

	private final ReactiveElasticsearchOperations operations;

	public ReactionService(ReactionRepository repository, ReactiveElasticsearchOperations operations) {
		super(repository, "reaction");
		this.operations = operations;
	}


	public Mono<Boolean> findByPost(String postId, String userId) {
		return repository.existsByFeedUserIdAndPostId(userId, postId);
	}

	public Mono<Void> deleteAllByPostId(String postId) {
		return repository.deleteAllByPostId(postId);
	}

	public Flux<FeedReaction> getAllReactions(Integer page, Integer size, String postId) {
		return ElasticsearchBuilder.create(
				FeedReaction.class,
				PageRequest.of(page, size),
				operations
		).modifyQuery(builder ->
				builder.must(mustBuilder -> mustBuilder.term(TermQuery.of(
										termQueryBuilder -> termQueryBuilder.field("postId.keyword").value(postId)
								)
						)
				)
		).doSearch();
	}

}
