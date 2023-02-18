package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.config.KeycloakConfig;
import git.dimitrikvirik.userapi.model.domain.User;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

	private final Keycloak keycloak;

	private final KeycloakConfig keycloakConfig;
	private final RestTemplate restTemplate;


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
		RealmResource realmResource = keycloak.realm(keycloakConfig.getRealm());
		return realmResource.users();
	}

	public AccessTokenResponse getToken(String username, String password, Boolean rememberMe) {
		try {
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("grant_type", "password");
			formData.add("username", username);
			formData.add("password", password);
			formData.add("client_id", keycloakConfig.getClientId());
			formData.add("client_secret", keycloakConfig.getClientSecret());
			if (rememberMe)
				formData.add("scope", "offline_access");
			return getAccessTokenResponse(formData);
		} catch (HttpClientErrorException e) {
			throw new ResponseStatusException(e.getStatusCode(), "Invalid username or password");
		}
	}

	public AccessTokenResponse refreshToken(String refreshToken) {
		try {
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("grant_type", "refresh_token");
			formData.add("refresh_token", refreshToken);
			formData.add("client_id", keycloakConfig.getClientId());
			formData.add("client_secret", keycloakConfig.getClientSecret());

			return getAccessTokenResponse(formData);
		} catch (HttpClientErrorException e) {
			throw new ResponseStatusException(e.getStatusCode(), "Invalid username or password");
		}
	}

	private AccessTokenResponse getAccessTokenResponse(MultiValueMap<String, String> formData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);


		ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
				String.format("%s/realms/%s/protocol/openid-connect/token", keycloakConfig.getKeycloakServerUrl(), keycloakConfig.getRealm()),
				HttpMethod.POST,
				entity,
				AccessTokenResponse.class
		);
		return response.getBody();
	}
}


