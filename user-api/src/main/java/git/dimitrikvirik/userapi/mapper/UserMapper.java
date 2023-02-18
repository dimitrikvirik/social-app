package git.dimitrikvirik.userapi.mapper;

import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.domain.User;

import java.time.ZoneOffset;

public class UserMapper {

	public static UserResponse toUserResponse(User user){
		return UserResponse
				.builder()
				.id(user.getId())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.email(user.getEmail())
				.build();
	}

}
