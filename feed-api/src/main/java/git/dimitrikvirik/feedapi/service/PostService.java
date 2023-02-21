package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;

	private final ReactiveElasticsearchOperations operations;


	public Mono<FeedPost> save(FeedPost post) {
		return postRepository.save(post);
	}

	public Mono<FeedPost> getById(String id) {
		return postRepository.findById(id);
	}

	public Mono<Void> deleteById(String id) {
		return postRepository.deleteById(id);
	}

	public Flux<FeedPost> getAll(Integer page, Integer size, String searchText) {
		return ElasticsearchBuilder.create(
						FeedPost.class,
						PageRequest.of(page, size),
						List.of("title", "content"),
						searchText,
						operations
				)
				.modifyQuery(builder -> {
					String createdAt = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
					builder.must(RangeQuery.of(rangeBuilder -> rangeBuilder.field("createdAt").from(createdAt))._toQuery());
					return builder;
				}).doSearch();
	}

	public Mono<Void> delete(FeedPost post) {
		return postRepository.delete(post);
	}
}
