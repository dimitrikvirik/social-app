package git.dimitrikvirik.notificationapi.config;

import com.sun.security.auth.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class KeycloakWebSocketAuthManager implements AuthenticationManager {

	private final KeycloakTokenVerifier tokenVerifier;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		JWSAuthenticationToken token = (JWSAuthenticationToken) authentication;
		String tokenString = (String) token.getCredentials();
		try {
			AccessToken accessToken = tokenVerifier.verifyToken(tokenString);
			List<GrantedAuthority> authorities = accessToken.getRealmAccess().getRoles().stream()
					.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

			String userId = (String) accessToken.getOtherClaims().get("userId");
			token = new JWSAuthenticationToken(tokenString, new UserPrincipal(userId), authorities);
			token.setAuthenticated(true);
		} catch (VerificationException e) {
			log.debug("Exception authenticating the token {}:", tokenString, e);
			throw new BadCredentialsException("Invalid token");
		}
		return token;
	}

}
