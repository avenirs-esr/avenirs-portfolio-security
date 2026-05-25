package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.authentication.domain.service.OidcServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OidcServiceConfig {

  @Bean
  OidcService oidcService(OidcAuthenticationPort oidcAuthenticationPort) {
    return new OidcServiceImpl(oidcAuthenticationPort);
  }
}
