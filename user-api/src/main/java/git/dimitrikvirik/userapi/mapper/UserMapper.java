package git.dimitrikvirik.userapi.mapper;

import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.model.kafka.UserDTO;

import java.time.ZoneOffset;

public class UserMapper {

	public static UserResponse toUserResponse(User user) {
		return UserResponse
				.builder()
				.id(user.getId())
				.photo(user.getProfile())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.build();
	}

	public static UserResponse toFullUserResponse(User user) {
		return UserResponse
				.builder()
				.id(user.getId())
				.photo(user.getProfile())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.email(user.getEmail())
				.createdAt(user.getCreatedAt().atOffset(ZoneOffset.UTC))
				.updatedAt(user.getUpdatedAt().atOffset(ZoneOffset.UTC))
				.build();
	}

	public static UserDTO toUserDTO(User user) {
		return new UserDTO(
				user.getId(),
				user.getFirstname(),
				user.getLastname(),
				user.getEmail(),
				user.getProfile()
		);
	}
}
