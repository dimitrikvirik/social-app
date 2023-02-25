package git.dimitrikvirik.notificationapi.config;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;

public class JWSAuthenticationToken extends AbstractAuthenticationToken implements Authentication {

	@Serial
	private static final long serialVersionUID = 1L;

	private final UserPrincipal principal;


	private final String token;

	public JWSAuthenticationToken(String token) {
		this(token, null, null);
	}

	public JWSAuthenticationToken(String token, UserPrincipal principal, Collection<GrantedAuthority> authorities) {
		super(authorities);
		this.token = token;
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

}
