package git.dimitrikvirik.feedapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocConfig {

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;


	@Bean
	OpenAPI openAPI() {
		String authUrl = issuerUri + "/protocol/openid-connect";

		Components components = new Components()
				.addSecuritySchemes("bearerAuth",
						new SecurityScheme()
								.name("bearerAuth")
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
				);
		components.addSecuritySchemes("spring_oauth", new SecurityScheme()
				.type(SecurityScheme.Type.OAUTH2)
				.description("Oauth2 flow")
				.flows(new OAuthFlows()
						.implicit(new OAuthFlow()
								.authorizationUrl(authUrl + "/auth")
								.scopes(new Scopes()
										.addString("offline_access", "offline_access")))));

		return new OpenAPI()
				.info(new Info()
						.title("Feed API")
						.version("1.0.0")
						.description("Feed API"))
				.servers(List.of(new Server().url("/feed/")))
				.components(
						components
				).security(List.of(new SecurityRequirement().addList("spring_oauth").addList("bearerAuth")));


	}

}
