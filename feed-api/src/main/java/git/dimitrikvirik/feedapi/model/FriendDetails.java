package git.dimitrikvirik.feedapi.model;

import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendDetails {
	private String userId;
	private FriendshipStatus status;
}
