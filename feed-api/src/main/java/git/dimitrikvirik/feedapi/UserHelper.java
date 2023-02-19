package git.dimitrikvirik.feedapi;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class UserHelper {

	public static Mono<String> currentUserId() {
		return jwt().map(jwt -> jwt.getClaimAsString("userId"));
	}


	public static Mono<Jwt> jwt() {
		return ReactiveSecurityContextHolder.getContext()
				.map(context -> context.getAuthentication().getPrincipal())
				.cast(Jwt.class);
	}

}
