package git.dimitrikvirik.feedapi.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//package git.dimitrikvirik.feedapi.config;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import reactor.core.publisher.Flux;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class KeycloakRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
//
//
//	@Override
//	public Flux<GrantedAuthority> convert(Jwt source) {
//
//
//		@SuppressWarnings("unchecked")
//		Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
//		if (realmAccess == null || realmAccess.isEmpty()) {
//			return Flux.empty();
//		}
//		@SuppressWarnings("unchecked")
//		List<SimpleGrantedAuthority> roles = ((List<String>) realmAccess.get("roles"))
//				.stream().map(roleName -> "ROLE_" + roleName)
//				.map(SimpleGrantedAuthority::new).toList();
//
//		return Flux.fromIterable(roles);
//	}
//}
public class KeycloakRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
	@Override
	public Flux<GrantedAuthority> convert(Jwt source) {

		@SuppressWarnings("unchecked")
		Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
		if (realmAccess == null || realmAccess.isEmpty()) {
			return Flux.empty();
		}
		@SuppressWarnings("unchecked")
		List<SimpleGrantedAuthority> roles = ((List<String>) realmAccess.get("roles"))
				.stream().map(roleName -> "ROLE_" + roleName)
				.map(SimpleGrantedAuthority::new).toList();

		return Flux.fromIterable(roles);
	}
}
