package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACActionRouteService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACActionRouteServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACActionRouteServiceConfig {
  @Bean
  public RBACActionRouteService rbacActionRouteService(
      RBACActionRouteRepository actionRouteRepository) {
    return new RBACActionRouteServiceImpl(actionRouteRepository);
  }
}
