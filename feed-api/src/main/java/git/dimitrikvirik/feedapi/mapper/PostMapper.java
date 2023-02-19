package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import org.springframework.http.ResponseEntity;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;


public class PostMapper {

	public static ResponseEntity<PostResponse> toPostResponseEntity(FeedPost post) {
		return ResponseEntity.ok(toPostResponse(post));
	}

	public static PostResponse toPostResponse(FeedPost post) {
		return PostResponse.builder()
				.title(post.getTitle())
				.id(post.getId())
				.user(UserMapper.fromUser(post.getUser()))
				.topics(post.getTopics())
				.content(post.getContent()).build();
	}


}
