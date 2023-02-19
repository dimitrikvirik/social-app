package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.User;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;

public class UserMapper {

	public static UserResponse fromUser(User user) {
		return UserResponse.builder()
				.id(user.getId())
				.firstname(user.getFirstname())
				.lastname(user.getLastname())
				.photo(user.getPhoto())
				.build();
	}

}
