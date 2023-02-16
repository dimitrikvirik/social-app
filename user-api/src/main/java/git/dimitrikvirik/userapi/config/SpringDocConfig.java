package git.dimitrikvirik.userapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

	@Bean
	OpenAPI openAPI(){
		return new OpenAPI()
				.info(new Info()
						.title("User API")
						.version("1.0.0")
						.description("User API"));
	}

}
