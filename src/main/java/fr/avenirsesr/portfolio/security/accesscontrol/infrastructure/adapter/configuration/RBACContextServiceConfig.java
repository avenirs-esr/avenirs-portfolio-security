package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACContextService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACContextRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACContextServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACContextServiceConfig {
  @Bean
  public RBACContextService rbacContextService(RBACContextRepository contextRepository) {
    return new RBACContextServiceImpl(contextRepository);
  }
}
