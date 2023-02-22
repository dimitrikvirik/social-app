package git.dimitrikvirik.feedapi.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.util.ObjectBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.springframework.data.elasticsearch.core.query.Query.SearchType.DFS_QUERY_THEN_FETCH;

public class ElasticsearchBuilder {

	@Data
	@Getter
	@Setter
	public static class Builder<T> {

		private final Class<T> clazz;

		private final PageRequest pageRequest;

		private final List<String> searchingFields;

		private final String searchText;

		private final ReactiveElasticsearchOperations operations;


		private Function<BoolQuery.Builder, ObjectBuilder<BoolQuery>> builder;

		public Builder<T> modifyQuery(Function<BoolQuery.Builder, BoolQuery.Builder> fn) {
			if (builder == null)
				initBuilder();


			builder = builder.compose(fn);
			return this;
		}

		private void initBuilder() {
			builder = (builder) -> {

				if (searchText == null || searchText.isBlank()) {
					builder.must(MatchAllQuery.of(matchBuilder -> matchBuilder.boost(1.0f))._toQuery());
				} else {
					builder.minimumShouldMatch("1")
							.should(
									searchingFields.stream()
											.map(field -> MatchQuery.of(shouldBuilder -> shouldBuilder.field(field).query(searchText).fuzziness("2").operator(Operator.And).query(searchText))._toQuery())
											.toList()
							).boost(20.0f);
				}
				return builder;
			};
		}

		public Flux<T> doSearch() {
			if (builder == null)
				initBuilder();

			var boolQuery = BoolQuery.of(getBuilder());


			Sort sort = Sort.by(Sort.Direction.DESC, "_score");

			NativeQuery nativeQuery = new NativeQuery(boolQuery._toQuery());
			nativeQuery.setPageable(pageRequest);
			nativeQuery.addSort(sort);
			nativeQuery.setSearchType(DFS_QUERY_THEN_FETCH);

			return operations.search(nativeQuery, clazz).map(SearchHit::getContent);
		}


	}

	public static <T> Builder<T> create(Class<T> clazz, PageRequest pageRequest, ReactiveElasticsearchOperations operations) {
		return new Builder<>(clazz, pageRequest, Collections.emptyList(), null, operations);
	}

	public static <T> Builder<T> create(Class<T> clazz, PageRequest pageRequest, List<String> searchingFields, String searchText, ReactiveElasticsearchOperations operations) {
		return new Builder<>(clazz, pageRequest, searchingFields, searchText, operations);
	}


}
