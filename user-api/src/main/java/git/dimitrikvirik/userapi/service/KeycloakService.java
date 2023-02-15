package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.model.domain.User;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

	private final Keycloak keycloak;

	@Value("${keycloak.realm}")
	private String realm;

	private static CredentialRepresentation getCredentialRepresentation(String password) {
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
		credentialRepresentation.setValue(password);
		credentialRepresentation.setTemporary(false);
		return credentialRepresentation;
	}


	public void resetPassword(String keycloakId, String password) {
		CredentialRepresentation credentialRepresentation = getCredentialRepresentation(password);
		keycloak.realm("master").users().get(keycloakId).resetPassword(credentialRepresentation);
	}

	public String createUser(User user, String password) {
		// Get the UsersResource for the realm
		UsersResource usersResource = getUsersResource();


		CredentialRepresentation credentialRepresentation = getCredentialRepresentation(password);
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(user.getEmail());
		userRepresentation.setUsername(user.getEmail());
		userRepresentation.setFirstName(user.getFirstname());
		userRepresentation.setLastName(user.getLastname());
		userRepresentation.setEmailVerified(true);

		HashMap<String, List<String>> attributes = new HashMap<>();
		attributes.put("userId", List.of(user.getId()));
		userRepresentation.setAttributes(attributes);
		userRepresentation.setEnabled(true);
		userRepresentation.setCredentials(List.of(credentialRepresentation));


		Response response = usersResource.create(userRepresentation);
		if (response.getStatus() != 201) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user: " + response.getStatusInfo().getReasonPhrase());
		}
		return CreatedResponseUtil.getCreatedId(response);

	}

	private UsersResource getUsersResource() {
		RealmResource realmResource = keycloak.realm(realm);
		return realmResource.users();
	}
}
