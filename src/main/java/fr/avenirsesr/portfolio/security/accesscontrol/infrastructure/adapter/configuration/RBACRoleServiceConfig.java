package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACRoleService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACRoleServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACRoleServiceConfig {
  @Bean
  public RBACRoleService rbacRoleService(
      RBACAssignmentRepository assignmentRepository, RBACRoleRepository roleRepository) {
    return new RBACRoleServiceImpl(roleRepository, assignmentRepository);
  }
}
