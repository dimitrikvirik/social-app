package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import git.dimitrikvirik.feedapi.utils.TimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostService extends AbstractUserService<FeedPost, PostRepository> {
	private final ReactiveElasticsearchOperations operations;

	public PostService(PostRepository postRepository, ReactiveElasticsearchOperations operations) {
		super(postRepository);
		this.operations = operations;
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
					String createdAt = ZonedDateTime.now().minusDays(2).format(TimeFormat.zoneDateTime);
					builder.must(RangeQuery.of(rangeBuilder -> rangeBuilder.field("createdAt").from(createdAt))._toQuery());
					return builder;
				}).doSearch();
	}


}
