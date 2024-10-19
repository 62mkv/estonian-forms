package ee.mkv.estonian.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "ee.mkv.estonian.repository")
public class JpaConfiguration {
}
