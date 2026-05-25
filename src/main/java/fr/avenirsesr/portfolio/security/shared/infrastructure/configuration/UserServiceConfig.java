package fr.avenirsesr.portfolio.security.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.common.user.infrastructure.service.NoOpUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {

  // TODO: remove this mock in #1683
  public static final String USER_ID_MOCK = "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";

  @Bean
  public BaseUserService baseUserService() {
    return new NoOpUserService();
  }
}
