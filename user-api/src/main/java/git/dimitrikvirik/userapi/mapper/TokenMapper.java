package git.dimitrikvirik.userapi.mapper;

import git.dimitrikvirik.userapi.model.JWTToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;

public class TokenMapper {

	public static JWTToken fromKeycloak(AccessTokenResponse accessTokenResponse) {
		return JWTToken.builder()
				.token(accessTokenResponse.getToken())
				.refreshToken(accessTokenResponse.getRefreshToken())
				.tokenValidTime((int) accessTokenResponse.getExpiresIn())
				.refreshTokenValidTime((int) accessTokenResponse.getRefreshExpiresIn())
				.build();
	}

}
