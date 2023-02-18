package git.dimitrikvirik.userapi.facade;

import git.dimitrikvirik.userapi.mapper.UserMapper;
import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.UserUpdateRequest;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.service.KeycloakService;
import git.dimitrikvirik.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	private final KeycloakService keycloakService;

	public UserResponse getCurrentUser() {
		String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();


		User user = userService.findByKeycloakKId(keycloakId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		);


		return UserMapper.toFullUserResponse(user);

	}


	public UserResponse getUser(String id) {
		User user = userService.findById(id);
		return UserMapper.toUserResponse(user);
	}

	public void blockUser(String id) {
		User user = userService.findById(id);
		user.setIsBlocked(true);
		userService.save(user);
	}

	public void unblockUser(String id) {
		User user = userService.findById(id);
		user.setIsBlocked(false);
		userService.save(user);
	}

	public void deleteUser(String id) {
		User user = userService.findById(id);

		user.setIsDisabled(true);
		userService.save(user);
	}

	public UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest) {
		User user = userService.findById(id);
		user.setFirstname(userUpdateRequest.getFirstName());
		user.setLastname(userUpdateRequest.getLastName());
		user.getUserPref().setCommentNotificationEnabled(userUpdateRequest.getCommentNotification());
		user.getUserPref().setLikeNotificationEnabled(userUpdateRequest.getLikeNotification());
		return UserMapper.toFullUserResponse(userService.save(user));
	}
}
