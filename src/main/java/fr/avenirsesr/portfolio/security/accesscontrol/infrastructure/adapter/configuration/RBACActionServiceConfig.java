package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACActionService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACActionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACActionServiceConfig {
  @Bean
  public RBACActionService rbacActionService(RBACActionRepository actionRepository) {
    return new RBACActionServiceImpl(actionRepository);
  }
}
