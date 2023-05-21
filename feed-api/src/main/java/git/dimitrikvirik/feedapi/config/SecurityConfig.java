package git.dimitrikvirik.feedapi.config;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern("*");
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


	@Bean
	public SecurityWebFilterChain chain(ServerHttpSecurity http) {

		var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());


		http
				.csrf().disable()
				.cors().configurationSource(corsConfigurationSource()).and()
				.authorizeExchange()
				.pathMatchers("/api-docs", "/test")
				.permitAll()
				.pathMatchers(HttpMethod.DELETE, "/topic/**").hasRole("admin")
				.pathMatchers(HttpMethod.POST, "/topic").hasRole("admin")
				.pathMatchers(HttpMethod.PUT, "/topic/**").hasRole("admin")
				.anyExchange().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt().jwtA
	uthenticationConverter(jwtAuthenticationConverter);

		return http.build();
	}

}
