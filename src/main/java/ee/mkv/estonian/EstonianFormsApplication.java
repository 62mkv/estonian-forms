package ee.mkv.estonian;

import ee.mkv.estonian.service.FileLoadService;
import ee.mkv.estonian.service.LoadMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
public class EstonianFormsApplication implements CommandLineRunner {

    @Autowired
    FileLoadService service;

    public static void main(String[] args) {
        SpringApplication.run(EstonianFormsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("EXECUTING : command line runner");
        for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }

        if (args.length > 0) {
            final String path = args[0];
            boolean loadArgs = false;
            if (args.length > 1) {
                loadArgs = "--all".equalsIgnoreCase(args[1]);
            }
            if (Objects.nonNull(path)) {
                service.loadFilesFromPath(path, Collections.emptySet());
            }
        }
    }
}
