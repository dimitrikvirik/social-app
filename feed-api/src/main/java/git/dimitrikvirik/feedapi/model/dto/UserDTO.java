package git.dimitrikvirik.feedapi.model.dto;

import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String id;
	private String firstname;
	private String lastname;
	private String photo;
	private FriendshipStatus friendshipStatus;
}
