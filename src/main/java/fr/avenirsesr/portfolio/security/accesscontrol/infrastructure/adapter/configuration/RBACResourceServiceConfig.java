package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACResourceService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACResourceServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACResourceServiceConfig {
  @Bean
  public RBACResourceService rbacResourceService(RBACResourceRepository resourceRepository) {
    return new RBACResourceServiceImpl(resourceRepository);
  }
}
