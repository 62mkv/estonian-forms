package ee.mkv.estonian.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(EkiLexConfiguration.class)
@ComponentScan(basePackages = {"ee.mkv.estonian.command.split", "ee.mkv.estonian.service", "ee.mkv.estonian.split"})
public class SplitConfiguration {
}
