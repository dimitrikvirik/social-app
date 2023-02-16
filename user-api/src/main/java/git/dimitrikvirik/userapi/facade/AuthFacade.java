package git.dimitrikvirik.userapi.facade;

import git.dimitrikvirik.userapi.model.*;
import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.ForgetPasswordRequest;
import git.dimitrikvirik.userapi.model.LoginRequest;
import git.dimitrikvirik.userapi.model.LoginResponse;
import git.dimitrikvirik.userapi.model.RestoreAccountRequest;
import git.dimitrikvirik.userapi.model.domain.User;
import git.dimitrikvirik.userapi.service.EmailCodeService;
import git.dimitrikvirik.userapi.service.KeycloakService;
import git.dimitrikvirik.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthFacade {

	private final UserService userService;
	private final EmailCodeService emailCodeService;

	private final KeycloakService keycloakService;

	public void emailValidation(EmailValidationRequest emailValidationRequest) {
		if (emailCodeService.getByEmail(emailValidationRequest.getEmail()).isPresent()) {
			emailCodeService.delete(emailValidationRequest.getEmail());
		}
		if (emailValidationRequest.getType().equals(EmailValidationRequest.TypeEnum.REGISTER) &&
				userService.existsByEmail(emailValidationRequest.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
		} else if (!userService.existsByEmail(emailValidationRequest.getEmail())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found");
		}

		emailCodeService.create(emailValidationRequest.getEmail(), emailValidationRequest.getType());
	}

	public void forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
		emailCodeService.check(forgetPasswordRequest.getEmail(), forgetPasswordRequest.getEmailCode(), EmailValidationRequest.TypeEnum.RESETPASSWORD);
		User user = userService.findByEmail(forgetPasswordRequest.getEmail()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User by email not found")
		);
		keycloakService.resetPassword(user.getKeycloakId(), forgetPasswordRequest.getPassword());
	}

	public LoginResponse login(LoginRequest loginRequest) {
		return null;
	}

	public void register(git.dimitrikvirik.userapi.model.RegisterRequest registerRequest) {
		emailCodeService.check(registerRequest.getEmail(), registerRequest.getEmailCode(), EmailValidationRequest.TypeEnum.REGISTER);
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setFirstname(registerRequest.getFirstName());
		user.setLastname(registerRequest.getLastName());
		user.setIsDisabled(false);
		user.setIsBlocked(false);
		userService.save(user);
		String keycloakId = keycloakService.createUser(user, registerRequest.getPassword());
		user.setKeycloakId(keycloakId);
		userService.save(user);
	}

	public void restoreAccount(RestoreAccountRequest restoreAccountRequest) {

	}
}
