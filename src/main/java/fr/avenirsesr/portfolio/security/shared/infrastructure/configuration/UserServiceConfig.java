package fr.avenirsesr.portfolio.security.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.common.user.infrastructure.service.NoOpUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {
  @Bean
  public BaseUserService baseUserService() {
    return new NoOpUserService();
  }
}
