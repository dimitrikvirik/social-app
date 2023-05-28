package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;

public class UserMapper {

	public static UserResponse toUserResponse(FeedUser feedUser) {
		return UserResponse.builder()
				.id(feedUser.getId())
				.firstname(feedUser.getFirstname())
				.lastname(feedUser.getLastname())
				.photo(feedUser.getPhoto())
				.build();
	}

	public static UserDTO toUserDTO(FeedUser feedUser, FriendshipStatus status) {
		return UserDTO.builder()
				.id(feedUser.getId())
				.firstname(feedUser.getFirstname())
				.lastname(feedUser.getLastname())
				.photo(feedUser.getPhoto())
				.friendshipStatus(status)
				.build();
	}

}
