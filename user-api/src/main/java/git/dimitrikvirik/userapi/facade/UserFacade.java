package git.dimitrikvirik.userapi.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.userapi.mapper.UserMapper;
import git.dimitrikvirik.userapi.model.ChangeEmailRequest;
import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.UserUpdateRequest;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import git.dimitrikvirik.userapi.model.kafka.UserDTO;
import git.dimitrikvirik.userapi.model.redis.EmailHash;
import git.dimitrikvirik.userapi.service.EmailHashService;
import git.dimitrikvirik.userapi.service.KeycloakService;
import git.dimitrikvirik.userapi.service.MinioService;
import git.dimitrikvirik.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	private final EmailHashService emailHashService;

	private final MinioService minioService;

	private final KeycloakService keycloakService;

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;


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
		keycloakService.logout(user.getKeycloakId());
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
		keycloakService.logout(user.getKeycloakId());
		userService.save(user);
	}

	public UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest) {
		User user = userService.findById(id);
		user.setFirstname(userUpdateRequest.getFirstName());
		user.setLastname(userUpdateRequest.getLastName());
		try {
			UserDTO userDTO = UserMapper.toUserDTO(user);
			kafkaTemplate.send("user", objectMapper.writeValueAsString(userDTO));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

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

	public UserResponse uploadUserPhoto(String id, MultipartFile file) {
		User user = userService.findById(id);
		String url = minioService.upload(file);
		user.setProfile(url);
		try {
			UserDTO userDTO = UserMapper.toUserDTO(user);
			kafkaTemplate.send("user", objectMapper.writeValueAsString(userDTO));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return UserMapper.toFullUserResponse(userService.save(user));
	}
}
