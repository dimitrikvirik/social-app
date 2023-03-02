package git.dimitrikvirik.notificationapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocConfig {

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Notification API")
						.version("1.0.0")
						.description("Notification API"))
				.servers(List.of(new io.swagger.v3.oas.models.servers.Server().url("/notification/")))
				.components(
						new Components()
								.addSecuritySchemes("bearerAuth",
										new SecurityScheme()
												.name("bearerAuth")
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				)
				.security(List.of(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth")));
	}

}
