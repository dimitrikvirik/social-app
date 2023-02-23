package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedComment;
import git.dimitrikvirik.feedapi.utils.TimeFormat;
import git.dimitrikvirik.generated.feedapi.model.CommentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommentMapper {

	public static CommentResponse toCommentResponse(FeedComment comment) {
		return CommentResponse.builder()
				.id(comment.getId())
				.user(UserMapper.fromUser(comment.getFeedUser()))
				.content(comment.getContent())
				.postId(comment.getPostId())
				.createdAt(comment.getCreatedAt().format(TimeFormat.zoneDateTime))
				.updatedAt(comment.getUpdatedAt().format(TimeFormat.zoneDateTime))
				.build();
	}

	public static ResponseEntity<CommentResponse> toCommentResponseEntityOk(FeedComment comment) {
		return ResponseEntity.ok(toCommentResponse(comment));
	}

	public static ResponseEntity<CommentResponse> toCommentResponseEntityCreated(FeedComment comment) {
		return new ResponseEntity<>(toCommentResponse(comment), HttpStatus.CREATED);
	}

}
