package git.dimitrikvirik.notificationapi.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserHelper {

	public static String currentUserId() {
		Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return principal.getClaimAsString("userId");
	}

}
