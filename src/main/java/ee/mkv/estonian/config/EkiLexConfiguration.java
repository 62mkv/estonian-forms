package ee.mkv.estonian.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty("ekilex.token")
@EnableConfigurationProperties(EkiLixProperties.class)
public class EkiLexConfiguration {

    @Bean
    public RestTemplate restTemplate(EkiLixProperties properties) {
        if (StringUtils.isBlank(properties.getUri()) || StringUtils.isBlank(properties.getToken())) {
            throw new RuntimeException("EkiLex URI and token MUST be provided");
        }
        return new RestTemplateBuilder()
                .defaultHeader("ekilex-api-key", properties.getToken())
                .rootUri(properties.getUri())
                .build();
    }
}
