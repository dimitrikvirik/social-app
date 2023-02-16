package git.dimitrikvirik.userapi.controller;

import git.dimitrikvirik.userapi.api.AuthApi;
import git.dimitrikvirik.userapi.facade.AuthFacade;
import git.dimitrikvirik.userapi.model.*;
import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.ForgetPasswordRequest;
import git.dimitrikvirik.userapi.model.LoginRequest;
import git.dimitrikvirik.userapi.model.LoginResponse;
import git.dimitrikvirik.userapi.model.RestoreAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthFacade authFacade;

	@Override
	public ResponseEntity<Void> emailValidation(EmailValidationRequest emailValidationRequest) {
		authFacade.emailValidation(emailValidationRequest);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
		authFacade.forgetPassword(forgetPasswordRequest);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
		return new ResponseEntity<>(authFacade.login(loginRequest), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> register(git.dimitrikvirik.userapi.model.RegisterRequest registerRequest) {
		authFacade.register(registerRequest);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> restoreAccount(RestoreAccountRequest restoreAccountRequest) {
		authFacade.restoreAccount(restoreAccountRequest);
		return ResponseEntity.ok().build();
	}
}
