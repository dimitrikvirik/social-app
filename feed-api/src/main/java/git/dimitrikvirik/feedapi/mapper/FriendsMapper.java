package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;

public class FriendsMapper {

	public static FeedFriends toFeedFriends(String userOneId, String userTwoId, FriendshipStatus status) {
		return FeedFriends.builder()
			.userOneId(userOneId)
			.userTwoId(userTwoId)
			.status(status)
			.build();
	}

}
