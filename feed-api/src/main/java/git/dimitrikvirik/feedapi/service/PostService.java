package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.ScoreSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.elasticsearch.core.query.Query.SearchType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.elasticsearch.core.query.Query.SearchType.DFS_QUERY_THEN_FETCH;

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

	public void deleteById(String id) {
		postRepository.deleteById(id).subscribe();
	}

	public Flux<FeedPost> getAll(Integer page, Integer size, String searchText) {
		var boolQuery = BoolQuery.of((builder -> {
			List<Query> mustQueries = new ArrayList<>();

			if (searchText == null || searchText.isBlank()) {
				mustQueries.add(MatchAllQuery.of(matchBuilder -> matchBuilder.boost(1.0f))._toQuery());


			} else {
				builder.minimumShouldMatch("1")
						.should(
								MatchQuery.of(shouldBuilder -> shouldBuilder.field("title").query(searchText).fuzziness("2").operator(Operator.And).query(searchText))._toQuery(),
								MatchQuery.of(shouldBuilder -> shouldBuilder.field("content").query(searchText).fuzziness("2").operator(Operator.And).query(searchText))._toQuery()
						).boost(1.0f);
			}

			String createdAt = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			mustQueries.add(RangeQuery.of(rangeBuilder -> rangeBuilder.field("createdAt").from(createdAt))._toQuery());
			builder.must(mustQueries);

			return builder;
		}));

		Sort sort = Sort.by(Sort.Direction.DESC, "_score");

		NativeQuery nativeQuery = new NativeQuery(boolQuery._toQuery());
		nativeQuery.setPageable(PageRequest.of(page, size));
		nativeQuery.addSort(sort);
		nativeQuery.setSearchType(DFS_QUERY_THEN_FETCH);


		return operations.search(nativeQuery, FeedPost.class).map(SearchHit::getContent);
	}

}
