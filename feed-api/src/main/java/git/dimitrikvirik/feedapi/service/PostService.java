package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import git.dimitrikvirik.feedapi.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PostService extends AbstractUserService<FeedPost, PostRepository> {
	private final ReactiveElasticsearchOperations operations;
	private static final String BOOST_SCRIPT =
		"""
			def score = _score;
			 score +=  doc['paymentBoost'].size() == 0 ? 1 : doc['paymentBoost'].value * %s
			         ;
			         def disAndLike = ((doc['like'].value * 1.5 )- doc['dislike'].value);
			           if(disAndLike > 0 ){
			              score += disAndLike;
			           }
			         score;
			\s
			""";

	@Value("${feed.paymentBoostingCoef}")
	private String paymentBoostingCoef;

	public PostService(PostRepository postRepository, ReactiveElasticsearchOperations operations) {
		super(postRepository, "post");
		this.operations = operations;
	}


	public Flux<FeedPost> getAll(Integer page, Integer size, OffsetDateTime createdAtBefore, List<String> topics, String searchText) {
		ElasticsearchBuilder.Builder<FeedPost> postBuilder = ElasticsearchBuilder.create(
				FeedPost.class,
				PageRequest.of(page, size, Sort.by("createdAt").descending()),
				List.of("title", "content"),
				searchText,
				operations
			)
			.minimumShouldMatch(2)
			.should(FunctionScoreQuery.of(functionScoreBuilder -> functionScoreBuilder
				.functions(FunctionScore.of(functionBuilder ->
					functionBuilder.scriptScore(ScriptScoreFunction.of(
						scriptScore -> scriptScore.script(Script.of(
							script -> script.inline(
								inline ->
									inline.source(String.format(BOOST_SCRIPT, paymentBoostingCoef))
							)
						)))
					))
				))._toQuery()
			);

		if (createdAtBefore != null)
			postBuilder.must(RangeQuery.of(rangeBuilder -> rangeBuilder.field("createdAt").to(createdAtBefore.format(TimeFormat.zoneDateTime)))._toQuery());
		topics.forEach(topic -> postBuilder.must(TermQuery.of(termBuilder -> termBuilder.field("topics.id.keyword").value(topic))._toQuery()));

		return postBuilder
			.doSearch();
	}


}
