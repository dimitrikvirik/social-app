package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.generated.feedapi.model.TopicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;

import java.util.stream.Collectors;


public class PostMapper {

	public static ResponseEntity<PostResponse> toPostResponseEntityOk(FeedPost post) {
		return toPostResponseEntity(post, HttpStatus.OK);
	}

	public static ResponseEntity<PostResponse> toPostResponseEntityCreated(FeedPost post) {
		return toPostResponseEntity(post, HttpStatus.CREATED);
	}

	public static ResponseEntity<PostResponse> toPostResponseEntity(FeedPost post, HttpStatus status) {
		return new ResponseEntity<>(toPostResponse(post), status);
	}

	public static PostResponse toPostResponse(FeedPost post) {
		return PostResponse.builder()
				.title(post.getTitle())
				.id(post.getId())
				.user(UserMapper.fromUser(post.getFeedUser()))
				.topics(post.getTopics().stream().map(feedTopic ->
						TopicResponse
								.builder()
								.id(feedTopic.getId())
								.name(feedTopic.getName())
								.build()).collect(Collectors.toList())
				)
				.content(post.getContent()).build();
	}


	public static ResponseEntity<Void> toPostResponseEntityNoContent(Void unused) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
