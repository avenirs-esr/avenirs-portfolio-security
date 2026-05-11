package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.domain.service.PrincipalServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrincipalServiceConfig {
  @Bean
  public PrincipalService principalService(PrincipalRepository principalRepository) {
    return new PrincipalServiceImpl(principalRepository);
  }
}
