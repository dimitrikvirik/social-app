package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;

public class UserMapper {

	public static UserResponse fromUser(FeedUser feedUser) {
		return UserResponse.builder()
				.id(feedUser.getId())
				.firstname(feedUser.getFirstname())
				.lastname(feedUser.getLastname())
				.photo(feedUser.getPhoto())
				.build();
	}

}
