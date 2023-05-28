package git.dimitrikvirik.feedapi.model.domain;

import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

@Document(indexName = "feed_friends")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedFriends {

	@Id
	private String id;

	@Field(type = FieldType.Keyword, name = "userOneId")
	private String userOneId;

	@Field(type = FieldType.Keyword, name = "userTwoId")
	private String userTwoId;

	@Field(type = FieldType.Keyword, name = "status")
	private FriendshipStatus status;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime updatedAt;


}
