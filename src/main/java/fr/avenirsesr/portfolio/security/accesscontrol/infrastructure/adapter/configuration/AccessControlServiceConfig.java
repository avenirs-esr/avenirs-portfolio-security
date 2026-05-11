package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.configuration;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.AccessControlService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.service.AccessControlServiceImpl;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessControlServiceConfig {
  @Bean
  public AccessControlService accessControlService(
      RBACActionRepository actionRepository,
      RBACAssignmentRepository assignmentRepository,
      PrincipalRepository principalRepository,
      RBACResourceRepository resourceRepository,
      RBACRoleRepository roleRepository,
      StructureRepository structureRepository,
      @Value("${avenirs.access.control.date.format}") String dateFormat) {

    return new AccessControlServiceImpl(
        actionRepository,
        assignmentRepository,
        principalRepository,
        resourceRepository,
        roleRepository,
        structureRepository,
        dateFormat);
  }
}
