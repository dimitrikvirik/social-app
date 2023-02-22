package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.generated.feedapi.model.TopicRequest;
import git.dimitrikvirik.generated.feedapi.model.TopicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class TopicMapper {


	public static FeedTopic toFeedTopic(TopicRequest topicRequest) {
		return FeedTopic.builder()
				.name(topicRequest.getName())
				.build();
	}

	public static TopicResponse toTopicResponse(FeedTopic feedTopic) {
		return TopicResponse.builder()
				.id(feedTopic.getId())
				.name(feedTopic.getName())
				.build();
	}

	public static ResponseEntity<TopicResponse> toTopicResponseEntity(FeedTopic feedTopic, HttpStatus status) {
		return new ResponseEntity<>(toTopicResponse(feedTopic), status);
	}

	public static ResponseEntity<TopicResponse> toTopicResponseEntityOK(FeedTopic feedTopic) {
		return toTopicResponseEntity(feedTopic, HttpStatus.OK);
	}

	public static ResponseEntity<TopicResponse> toTopicResponseEntityCreated(FeedTopic feedTopic) {
		return toTopicResponseEntity(feedTopic, HttpStatus.CREATED);
	}



}
