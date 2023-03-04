package git.dimitrikvirik.userapi.config;

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
		OpenAPI openAPI = new OpenAPI()
				.info(new Info()
						.title("User API")
						.version("1.0.0")
						.description("User API"))
				.servers(List.of(new Server().url("/user/")))
				.components(
						new Components()
								.addSecuritySchemes("bearerAuth",
										new SecurityScheme()
												.name("bearerAuth")
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				);
		addSecurity(openAPI);
		return openAPI;
	}

	private void addSecurity(OpenAPI openAPI) {


		String authUrl = issuerUri + "/protocol/openid-connect";

		openAPI.components(new Components()
						.addSecuritySchemes("spring_oauth", new SecurityScheme()
								.type(SecurityScheme.Type.OAUTH2)
								.description("Oauth2 flow")
								.flows(new OAuthFlows()
										.implicit(new OAuthFlow()
												.authorizationUrl(authUrl + "/auth")
												.scopes(new Scopes()
														.addString("openid", "openid"))))))
				.security(List.of(new SecurityRequirement().addList("spring_oauth")));
	}

}
