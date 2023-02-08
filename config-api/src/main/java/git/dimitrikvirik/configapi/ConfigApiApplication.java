package git.dimitrikvirik.configapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApiApplication.class, args);
    }

}
