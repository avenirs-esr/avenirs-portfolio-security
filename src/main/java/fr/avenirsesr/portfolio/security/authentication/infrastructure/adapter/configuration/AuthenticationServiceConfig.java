package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthContextSigningPort;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthorizationsPort;
import fr.avenirsesr.portfolio.security.authentication.domain.service.AuthenticationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceConfig {

  @Bean
  AuthenticationService authenticationService(
      OidcService oidcService,
      AuthContextSigningPort authContextSigningPort,
      AuthorizationsPort authorizationsPort) {
    return new AuthenticationServiceImpl(oidcService, authContextSigningPort, authorizationsPort);
  }
}
