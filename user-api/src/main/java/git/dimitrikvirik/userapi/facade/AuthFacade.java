package git.dimitrikvirik.userapi.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.userapi.mapper.TokenMapper;
import git.dimitrikvirik.userapi.mapper.UserMapper;
import git.dimitrikvirik.userapi.model.*;
import git.dimitrikvirik.userapi.model.EmailValidationApproveRequest;
import git.dimitrikvirik.userapi.model.EmailValidationApproveResponse;
import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.ForgetPasswordRequest;
import git.dimitrikvirik.userapi.model.JWTToken;
import git.dimitrikvirik.userapi.model.LoginRequest;
import git.dimitrikvirik.userapi.model.LoginResponse;
import git.dimitrikvirik.userapi.model.RefreshLoginRequest;
import git.dimitrikvirik.userapi.model.RestoreAccountRequest;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.model.domain.UserPref;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import git.dimitrikvirik.userapi.model.kafka.UserDTO;
import git.dimitrikvirik.userapi.model.redis.EmailHash;
import git.dimitrikvirik.userapi.service.EmailCodeService;
import git.dimitrikvirik.userapi.service.EmailHashService;
import git.dimitrikvirik.userapi.service.KeycloakService;
import git.dimitrikvirik.userapi.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthFacade {

	private final UserService userService;
	private final EmailCodeService emailCodeService;

	private final EmailHashService emailHashService;
	private final KeycloakService keycloakService;

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;


	public void emailValidation(EmailValidationRequest emailValidationRequest) {
		if (emailCodeService.getByEmail(emailValidationRequest.getEmail()).isPresent()) {
			emailCodeService.delete(emailValidationRequest.getEmail());
		}
		if (emailValidationRequest.getType().equals(EmailValidationRequest.TypeEnum.REGISTER) &&
				userService.existsByEmail(emailValidationRequest.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
		} else if (!emailValidationRequest.getType().equals(EmailValidationRequest.TypeEnum.REGISTER) && !userService.existsByEmail(emailValidationRequest.getEmail())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found");
		}

		emailCodeService.create(emailValidationRequest.getEmail(), emailValidationRequest.getType());
	}

	public void forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
		EmailHash emailHash = emailHashService.getById(forgetPasswordRequest.getEmailHash());
		if (!emailHash.getType().equals(EmailType.RESETPASSWORD))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email hash is not for register");
		User user = userService.findByEmail(emailHash.getEmail()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.GONE, "User not found")
		);
		keycloakService.resetPassword(user.getKeycloakId(), forgetPasswordRequest.getPassword());
	}

	public LoginResponse login(LoginRequest loginRequest) {
		AccessTokenResponse serviceToken = keycloakService.getToken(loginRequest.getEmail(), loginRequest.getPassword(), loginRequest.getRememberMe());
		User user = userService.findByEmail(loginRequest.getEmail()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.GONE, "User not found")
		);
		if (user.getIsBlocked())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked");
		if (user.getIsDisabled())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is disabled");

		return  LoginResponse
				.builder()
				.jwt(TokenMapper.fromKeycloak(serviceToken))
				.user(UserMapper.toFullUserResponse(user))
				.build();
	}

	public void register(git.dimitrikvirik.userapi.model.RegisterRequest registerRequest) {
		EmailHash emailHash = emailHashService.getById(registerRequest.getEmailHash());
		if (!emailHash.getType().equals(EmailType.REGISTER))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email hash is not for register");

		User user = new User();
		user.setEmail(emailHash.getEmail());
		user.setFirstname(registerRequest.getFirstName());
		user.setLastname(registerRequest.getLastName());
		user.setIsDisabled(false);
		user.setIsBlocked(false);
		user.setUserPref(new UserPref());
		userService.save(user);
		String keycloakId = keycloakService.createUser(user, registerRequest.getPassword());
		user.setKeycloakId(keycloakId);
		try {
			UserDTO userDTO = UserMapper.toUserDTO(user);
			kafkaTemplate.send("user", objectMapper.writeValueAsString(userDTO));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		userService.save(user);
	}

	public void restoreAccount(RestoreAccountRequest restoreAccountRequest) {
		EmailHash emailHash = emailHashService.getById(restoreAccountRequest.getEmailHash());
		if (!emailHash.getType().equals(EmailType.RESTOREACCOUNT))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email hash is not for register");
		User user = userService.findByEmail(emailHash.getEmail()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.GONE, "User not found")
		);
		user.setIsDisabled(false);
		userService.save(user);
	}

	public EmailValidationApproveResponse emailValidationApprove(EmailValidationApproveRequest emailValidationApproveRequest) {
		EmailType emailType = EmailType.valueOf(emailValidationApproveRequest.getType().name());
		emailCodeService.check(emailValidationApproveRequest.getEmail(), emailValidationApproveRequest.getEmailCode(), emailType);
		EmailHash emailHash = emailHashService.save(emailValidationApproveRequest.getEmail(), emailType);
		emailCodeService.delete(emailValidationApproveRequest.getEmail());

		return EmailValidationApproveResponse
				.builder()
				.emailHash(emailHash.getId())
				.build();
	}

	public JWTToken refreshLogin(RefreshLoginRequest refreshLoginRequest) {
		AccessTokenResponse serviceToken = keycloakService.refreshToken(refreshLoginRequest.getRefreshToken());
		return TokenMapper.fromKeycloak(serviceToken);
	}
}
