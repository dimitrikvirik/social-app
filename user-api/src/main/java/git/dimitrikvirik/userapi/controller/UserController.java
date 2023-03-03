package git.dimitrikvirik.userapi.controller;

import git.dimitrikvirik.userapi.api.UserApi;
import git.dimitrikvirik.userapi.facade.UserFacade;
import git.dimitrikvirik.userapi.model.ChangeEmailRequest;
import git.dimitrikvirik.userapi.model.UserResponse;
import git.dimitrikvirik.userapi.model.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

	private final UserFacade userFacade;

	@Override
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> blockUser(String id) {
		userFacade.blockUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<UserResponse> getCurrentUser() {
		return ResponseEntity.ok(userFacade.getCurrentUser());
	}

	@Override
	@PreAuthorize("@validPermission.sameOrAdmin(#id)")
	public ResponseEntity<Void> deleteUser(String id) {
		userFacade.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	@PreAuthorize("@validPermission.sameOrAdmin(#id)")
	public ResponseEntity<Void> changeEmail(String id, ChangeEmailRequest changeEmailRequest) {
		userFacade.changeEmail(id, changeEmailRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<UserResponse> getUser(String id) {
		return ResponseEntity.ok(userFacade.getUser(id));
	}

	@Override
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> unblockUser(String id) {
		userFacade.unblockUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	@PreAuthorize("@validPermission.sameOrAdmin(#id)")
	public ResponseEntity<UserResponse> updateUser(String id, UserUpdateRequest userUpdateRequest) {
		return ResponseEntity.ok(userFacade.updateUser(id, userUpdateRequest));
	}


	@Override
	@PreAuthorize("@validPermission.sameOrAdmin(#id)")
	public ResponseEntity<UserResponse> uploadUserPhoto(String id, MultipartFile file) {
		return ResponseEntity.ok(userFacade.uploadUserPhoto(id, file));
	}
}
