package git.dimitrikvirik.userapi.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.secret}")
	private String clientSecret;

	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				.serverUrl(keycloakServerUrl)
				.username("admin")
				.password("admin")
				.realm(realm)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.build();
	}


}
