package ee.mkv.estonian;

import ee.mkv.estonian.service.FileLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
public class EstonianFormsApplication {

    @Autowired
    FileLoadService service;

    public static void main(String[] args) {
        SpringApplication.run(EstonianFormsApplication.class, args);
    }

}
