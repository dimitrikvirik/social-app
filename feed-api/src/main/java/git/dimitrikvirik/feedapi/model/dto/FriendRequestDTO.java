package git.dimitrikvirik.feedapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDTO {

	private String requestId;
	private Boolean isReceiver;
	private String userId;
	private String firstname;
	private String lastname;
	private String photo;

}
