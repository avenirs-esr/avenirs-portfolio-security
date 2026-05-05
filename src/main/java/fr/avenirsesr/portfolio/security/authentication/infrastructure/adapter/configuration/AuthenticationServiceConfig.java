package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthenticationPort;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.authentication.domain.service.AuthenticationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceConfig {

  @Bean
  AuthenticationService authenticationService(
      AuthenticationPort authenticationPort, PrincipalRepository principalRepository) {
    return new AuthenticationServiceImpl(authenticationPort, principalRepository);
  }
}
