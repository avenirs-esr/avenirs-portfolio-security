package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACScopeService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACScopeRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACScopeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACScopeServiceConfig {
  @Bean
  public RBACScopeService rbacScopeService(RBACScopeRepository scopeRepository) {
    return new RBACScopeServiceImpl(scopeRepository);
  }
}
