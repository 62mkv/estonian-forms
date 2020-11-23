package ee.mkv.estonian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
public class EstonianFormsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstonianFormsApplication.class, args);
        System.exit(0);
    }

}
