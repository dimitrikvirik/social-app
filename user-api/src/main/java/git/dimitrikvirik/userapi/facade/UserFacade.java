package git.dimitrikvirik.userapi.facade;

import git.dimitrikvirik.userapi.mapper.UserMapper;
import git.dimitrikvirik.userapi.model.ChangeEmailRequest;
import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.UserUpdateRequest;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import git.dimitrikvirik.userapi.model.redis.EmailHash;
import git.dimitrikvirik.userapi.service.EmailHashService;
import git.dimitrikvirik.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	private final EmailHashService emailHashService;



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
	//TODO remove keycloak session
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


	//TODO remove keycloak session
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

	public void changeEmail(String id, ChangeEmailRequest changeEmailRequest) {
		EmailHash emailHash = emailHashService.getById(changeEmailRequest.getEmailHash());
		if (!emailHash.getType().equals(EmailType.CHANGEEMAIL))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email hash is not for change email	");

		User user = userService.findById(id);
		user.setEmail(emailHash.getEmail());
		userService.save(user);
	}
}
