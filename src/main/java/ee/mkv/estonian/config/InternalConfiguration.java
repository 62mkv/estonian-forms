package ee.mkv.estonian.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ee.mkv.estonian.command.internal", "ee.mkv.estonian.service"})
public class InternalConfiguration {

}
