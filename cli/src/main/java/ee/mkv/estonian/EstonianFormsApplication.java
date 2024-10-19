package ee.mkv.estonian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"ee.mkv.estonian.config", "com.kakawait.spring.boot.picocli.autoconfigure"})
@SpringBootApplication
public class EstonianFormsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstonianFormsApplication.class, args);
        System.exit(0);
    }

}
