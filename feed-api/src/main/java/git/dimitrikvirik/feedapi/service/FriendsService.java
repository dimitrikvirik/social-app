package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import git.dimitrikvirik.feedapi.mapper.FriendsMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import git.dimitrikvirik.feedapi.repository.FeedFriendsRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class FriendsService extends AbstractService<FeedFriends, FeedFriendsRepository> {

	private final ReactiveElasticsearchOperations operations;

	public FriendsService(FeedFriendsRepository repository, ReactiveElasticsearchOperations operations) {
		super(repository, "feed_friends");
		this.operations = operations;
	}

	public Mono<FeedFriends> addFriend(String userOneId, String userTwoId) {
		FeedFriends feedFriends = FriendsMapper.toFeedFriends(userOneId, userTwoId, FriendshipStatus.PENDING);

		return repository.save(feedFriends);
	}

	public Flux<FeedFriends> findActiveFriendsByUserId(String userId, Pageable pageRequest) {
		List<FriendshipStatus> statuses = List.of(FriendshipStatus.ACCEPTED, FriendshipStatus.PENDING);
		return operations.search(friendsOfUserQuery(userId, statuses, pageRequest), FeedFriends.class)
			.map(SearchHit::getContent);
	}

	public Mono<FeedFriends> findActiveFriendsByUserIds(String firstUserId, String secondUserId) {
		List<FriendshipStatus> statuses = List.of(FriendshipStatus.ACCEPTED, FriendshipStatus.PENDING);
		return operations.search(friendsQuery(firstUserId, secondUserId, statuses), FeedFriends.class)
			.map(SearchHit::getContent).next();
	}

	private Query friendsOfUserQuery(String userId, List<FriendshipStatus> statuses, Pageable pageRequest) {
		FieldValue userIdValue = FieldValue.of(userId);
		List<FieldValue> statusValues = statuses.stream().map(s -> FieldValue.of(s.name())).toList();

		return NativeQuery.builder()
			.withQuery(q -> q.bool(
				b -> b
					.should(s -> s.term(termQuery("userOneId", userIdValue)))
					.should(s -> s.term(termQuery("userTwoId", userIdValue)))
					.minimumShouldMatch("1")
					.filter(f -> f.terms(termsQuery("status", statusValues)))))
			.withPageable(pageRequest)
			.build();
	}

	private Query friendsQuery(String firstUserId, String secondUserId, List<FriendshipStatus> statuses) {
		FieldValue firstUserIdValue = FieldValue.of(firstUserId);
		FieldValue secondUserIdValue = FieldValue.of(secondUserId);
		List<FieldValue> statusValues = statuses.stream().map(s -> FieldValue.of(s.name())).toList();

		return NativeQuery.builder()
			.withQuery(q -> q.bool(
				b -> b
					.must(m -> m.bool(mq -> mq
						.should(s -> s.term(termQuery("userOneId", firstUserIdValue)))
						.should(s -> s.term(termQuery("userTwoId", firstUserIdValue)))
						.minimumShouldMatch("1")))
					.must(m -> m.bool(mq -> mq
						.should(s -> s.term(termQuery("userOneId", secondUserIdValue)))
						.should(s -> s.term(termQuery("userTwoId", secondUserIdValue)))
						.minimumShouldMatch("1")))
					.filter(f -> f.terms(termsQuery("status", statusValues)))))
			.build();
	}

	private TermQuery termQuery(String field, FieldValue value) {
		return TermQuery.of(t ->
			t.field(field)
				.value(value));
	}

	private TermsQuery termsQuery(String field, List<FieldValue> values) {
		return TermsQuery.of(t ->
			t.field(field)
				.terms(tv -> tv.value(values)));
	}

}
