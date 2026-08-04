package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RBACAssignmentSeeder {

  private final PrincipalRepository principalRepository;
  private final RBACRoleRepository roleRepository;
  private final RBACAssignmentRepository assignmentRepository;

  @Transactional
  public void seed() {
    List<Principal> principals = principalRepository.findAll();

    principals.forEach(this::seedAssignment);

    log.info("✔ {} principal role assignments seeded", principals.size());
  }

  private void seedAssignment(Principal principal) {
    ERole role = resolveRole(principal.getCategory());
    RBACRole rbacRole = roleRepository.findByName(role.name()).orElseThrow();

    assignmentRepository.save(
        new RBACAssignment(
            null,
            principal,
            rbacRole,
            new RBACScope(null, List.of()),
            new RBACContext(null, null, null, Set.of())));
  }

  private ERole resolveRole(EUserCategory category) {
    return switch (category) {
      case STUDENT -> ERole.ROLE_STUDENT;
      case STAFF -> ERole.ROLE_STAFF;
    };
  }
}
