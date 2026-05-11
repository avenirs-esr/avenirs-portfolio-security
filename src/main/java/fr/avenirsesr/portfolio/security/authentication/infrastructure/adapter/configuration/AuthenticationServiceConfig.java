package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthenticationPort;
import fr.avenirsesr.portfolio.security.authentication.domain.service.AuthenticationServiceImpl;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceConfig {

  @Bean
  AuthenticationService authenticationService(
      AuthenticationPort authenticationPort, PrincipalService principalService) {
    return new AuthenticationServiceImpl(authenticationPort, principalService);
  }
}
