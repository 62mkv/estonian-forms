package ee.mkv.estonian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"ee.mkv.estonian.config"})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
public class EstonianFormsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstonianFormsApplication.class, args);
        System.exit(0);
    }

}
