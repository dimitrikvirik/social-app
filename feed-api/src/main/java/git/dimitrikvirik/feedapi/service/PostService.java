package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.ScriptBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import git.dimitrikvirik.feedapi.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class PostService extends AbstractUserService<FeedPost, PostRepository> {
	private final ReactiveElasticsearchOperations operations;

	@Value("${feed.paymentBoostingCoef}")
	private String paymentBoostingCoef;

	public PostService(PostRepository postRepository, ReactiveElasticsearchOperations operations) {
		super(postRepository, "post");
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
					builder.should(FunctionScoreQuery.of(functionScoreBuilder -> functionScoreBuilder
							.functions(FunctionScore.of(functionBuilder ->
									functionBuilder.scriptScore(ScriptScoreFunction.of(
											scriptScore -> scriptScore.script(Script.of(
													script -> script.inline(
															inline ->
																	inline.source("""
																			def score = _score;
																				if(doc.containsKey('paymentBoost')){
																				  score *= doc['paymentBoost'] * """ + paymentBoostingCoef +  """
																				;}
																			     if(doc.containsKey('like') && doc.containsKey('dislike')){
																			              def disAndLike = ((doc['like'].value * 1.5 )- doc['dislike'].value);
																			              if(disAndLike > 0 ){
																			                 score *= disAndLike;
																			              }
																			            }
																				score											
																			""")
													)
											)))
									))
							))._toQuery()
					);
					return builder;
				}).doSearch();
	}


}
