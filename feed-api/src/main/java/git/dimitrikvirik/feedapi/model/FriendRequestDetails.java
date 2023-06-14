package git.dimitrikvirik.feedapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequestDetails {
	private String requestId;
	private String userId;
	private Boolean isReceiver;
}
