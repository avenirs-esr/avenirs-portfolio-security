package fr.avenirsesr.portfolio.security.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient(@Value("${avenirs.back-office.api-key}") String backOfficeApiKey) {
    return WebClient.builder()
        .defaultHeader(AvenirsSecurityHeaders.API_KEY, backOfficeApiKey)
        .build();
  }
}
