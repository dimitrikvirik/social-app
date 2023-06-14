package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.FriendRequestDetails;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.FriendRequestDTO;

public class FriendsMapper {

	private FriendsMapper() {}

	public static FriendRequestDTO toFriendRequestDTO(FeedUser user, FriendRequestDetails requestDetails) {
		return FriendRequestDTO.builder()
			.requestId(requestDetails.getRequestId())
			.userId(requestDetails.getUserId())
			.isReceiver(requestDetails.getIsReceiver())
			.firstname(user.getFirstname())
			.lastname(user.getLastname())
			.photo(user.getPhoto())
			.build();
	}

}
