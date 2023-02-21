package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.generated.feedapi.model.TopicResponse;
import org.springframework.http.ResponseEntity;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;

import java.util.stream.Collectors;


public class PostMapper {

	public static ResponseEntity<PostResponse> toPostResponseEntity(FeedPost post) {
		return ResponseEntity.ok(toPostResponse(post));
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


}
