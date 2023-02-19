package git.dimitrikvirik.feedapi.model.kafka;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDTO {

	private String id;

	private String firstName;

	private String lastName;

	private String email;

	private String profile;


}
