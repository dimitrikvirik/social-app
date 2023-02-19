package git.dimitrikvirik.feedapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {


	@Bean
	public SecurityWebFilterChain chain(ServerHttpSecurity http) {

		var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());


		http
				.csrf().disable()
				.cors().disable()
				.authorizeExchange()
				.pathMatchers("/api-docs")
				.permitAll()
				.pathMatchers("/test").hasRole("admin")
				.anyExchange().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter);

		return http.build();
	}

}
