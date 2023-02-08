package git.dimitrikvirik.eurekaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApiApplication.class, args);
    }

}
