package git.dimitrikvirik.userapi.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

	private String id;

	private String firstName;

	private String lastName;

	private String email;

	private String profile;

	private Boolean commentNotificationEnabled;

	private Boolean reactionNotificationEnabled;


}
