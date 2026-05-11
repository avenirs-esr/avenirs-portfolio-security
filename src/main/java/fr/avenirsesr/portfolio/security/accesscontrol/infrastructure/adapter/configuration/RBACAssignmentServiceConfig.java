package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACAssignmentService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.RBACAssignmentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBACAssignmentServiceConfig {
  @Bean
  public RBACAssignmentService rbacAssignmentService(
      RBACAssignmentRepository assignmentRepository) {
    return new RBACAssignmentServiceImpl(assignmentRepository);
  }
}
