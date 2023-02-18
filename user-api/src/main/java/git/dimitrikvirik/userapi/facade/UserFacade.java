package git.dimitrikvirik.userapi.facade;

import git.dimitrikvirik.userapi.mapper.UserMapper;
import git.dimitrikvirik.userapi.model.UserResponse;
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


		UserResponse userResponse = UserMapper.toUserResponse(user);
		userResponse.setCreatedAt(user.getCreatedAt().atOffset(ZoneOffset.UTC));
		userResponse.setUpdatedAt(user.getCreatedAt().atOffset(ZoneOffset.UTC));
		return userResponse;

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
		String currentKeycloakId = keycloakService.getCurrentKeycloakId();


		if (!user.getKeycloakId().equals(currentKeycloakId) && !keycloakService.isAdmin()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this user");
		}

		user.setIsDisabled(true);
		userService.save(user);
	}
}
