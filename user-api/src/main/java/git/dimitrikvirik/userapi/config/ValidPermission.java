package git.dimitrikvirik.userapi.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component("validPermission")
public class ValidPermission {

	public Boolean sameOrAdmin(String id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Optional<? extends GrantedAuthority> roleAdmin = authorities.stream().filter(authority -> authority.getAuthority().equals("ROLE_admin")).findAny();
		String userId = (String) ((Jwt) authentication.getCredentials()).getClaims().get("userId");
		return roleAdmin.isPresent() || userId.equals(id);
	}

}
