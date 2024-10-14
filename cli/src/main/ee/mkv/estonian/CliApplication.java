package ee.mkv.estonian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"ee.mkv.estonian.config"})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
@SpringBootApplication
public class CliApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.exit(0);
    }

}
