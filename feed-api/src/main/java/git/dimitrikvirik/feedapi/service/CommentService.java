package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import git.dimitrikvirik.feedapi.model.domain.FeedComment;
import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.repository.CommentRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommentService extends AbstractUserService<FeedComment, CommentRepository> {

	private final ReactiveElasticsearchOperations operations;

	public CommentService(CommentRepository repository, ReactiveElasticsearchOperations operations) {
		super(repository, "comment");
		this.operations = operations;
	}

	public Mono<Void> deleteAllByPostId(String id) {
		return repository.deleteByPostId(id);
	}

	public Flux<FeedComment> getAll(Integer page, Integer size, String postId) {
		return ElasticsearchBuilder.create(
						FeedComment.class,
						PageRequest.of(page, size),
						operations
				).must(TermQuery.of(
						termQueryBuilder -> termQueryBuilder.field("postId.keyword").value(postId))._toQuery())
				.doSearch();
	}
}
