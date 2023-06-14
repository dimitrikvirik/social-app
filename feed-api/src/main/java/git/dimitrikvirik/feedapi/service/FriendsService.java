package git.dimitrikvirik.feedapi.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FriendsService extends AbstractService<FeedFriends, FeedFriendsRepository> {

	private final ReactiveElasticsearchOperations operations;

	public FriendsService(FeedFriendsRepository repository, ReactiveElasticsearchOperations operations) {
		super(repository, "feed_friends");
		this.operations = operations;
	}

	public Mono<FeedFriends> createFriendRequest(String userOneId, String userTwoId) {
		return repository.save(createFeedFriends(userOneId, userTwoId));
	}

	public Mono<FeedFriends> findFriendRequest(String requestId, List<FriendshipStatus> statuses) {
		return repository.findByIdAndStatusIn(requestId, statuses);
	}

	public Mono<FeedFriends> updateFriendship(FeedFriends request, FriendshipStatus status) {
		request.setStatus(status);
		request.setUpdatedAt(ZonedDateTime.now());
		return repository.save(request);
	}

	public Flux<FeedFriends> findFriendshipsByUser(String userId,
												   List<FriendshipStatus> statuses,
												   Pageable pageRequest) {

		return operations.search(friendsOfUserQuery(userId, statuses, pageRequest), FeedFriends.class)
			.map(SearchHit::getContent);
	}

	public Mono<FeedFriends> findFriendshipForUsers(String firstUserId,
													String secondUserId,
													List<FriendshipStatus> statuses) {
		return operations.search(friendsQuery(firstUserId, secondUserId, statuses), FeedFriends.class)
			.map(SearchHit::getContent).next();
	}

	public Mono<FeedFriends> findIncomingRequest(String id, String receiverId, List<FriendshipStatus> statuses) {
		return operations.search(incomingRequestQuery(id, receiverId, statuses), FeedFriends.class)
			.map(SearchHit::getContent).next();
	}

	private FeedFriends createFeedFriends(String userOneId, String userTwoId) {
		return FeedFriends.builder()
			.id(UUID.randomUUID().toString())
			.userOneId(userOneId)
			.userTwoId(userTwoId)
			.status(FriendshipStatus.PENDING)
			.createdAt(ZonedDateTime.now())
			.build();
	}

	private Query friendsOfUserQuery(String userId, List<FriendshipStatus> statuses, Pageable pageRequest) {
		FieldValue userIdValue = FieldValue.of(userId);
		List<FieldValue> statusValues = statuses.stream().map(s -> FieldValue.of(s.name())).toList();

		return NativeQuery.builder()
			.withQuery(q -> q.bool(
				b -> b
					.should(termQuery("userOneId", userIdValue)._toQuery())
					.should(termQuery("userTwoId", userIdValue)._toQuery())
					.minimumShouldMatch("1")
					.filter(termsQuery("status", statusValues)._toQuery())))
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
						.should(termQuery("userOneId", firstUserIdValue)._toQuery())
						.should(termQuery("userTwoId", firstUserIdValue)._toQuery())
						.minimumShouldMatch("1")))
					.must(m -> m.bool(mq -> mq
						.should(termQuery("userOneId", secondUserIdValue)._toQuery())
						.should(termQuery("userTwoId", secondUserIdValue)._toQuery())
						.minimumShouldMatch("1")))
					.filter(termsQuery("status", statusValues)._toQuery())))
			.build();
	}

	private Query incomingRequestQuery(String id, String receiverId, List<FriendshipStatus> statuses) {
		FieldValue idValue = FieldValue.of(id);
		FieldValue userTwoIdValue = FieldValue.of(receiverId);
		List<FieldValue> statusValues = statuses.stream().map(s -> FieldValue.of(s.name())).toList();

		return NativeQuery.builder()
			.withQuery(q -> q.bool(
				b -> b
					.filter(termQuery("id", idValue)._toQuery())
					.filter(termQuery("userTwoId", userTwoIdValue)._toQuery())
					.filter(termsQuery("status", statusValues)._toQuery())))
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
