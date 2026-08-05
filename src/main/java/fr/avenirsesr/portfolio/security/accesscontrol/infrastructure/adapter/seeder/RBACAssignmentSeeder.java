package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RBACAssignmentSeeder {

  private final PrincipalRepository principalRepository;
  private final RBACRoleRepository roleRepository;
  private final RBACAssignmentRepository assignmentRepository;

  @Value("${seeder.rbac.super-admin-logins:}")
  private String superAdminLoginsProperty;

  @Transactional
  public void seed() {
    Set<String> superAdminLogins = parseSuperAdminLogins();
    List<Principal> principals = principalRepository.findAll();

    int studentCount = 0;
    int staffCount = 0;
    int superAdminCount = 0;

    for (Principal principal : principals) {
      ERole role = resolveRole(principal, superAdminLogins);
      seedAssignment(principal, role);

      switch (role) {
        case ROLE_SUPER_ADMIN -> superAdminCount++;
        case ROLE_STAFF -> staffCount++;
        case ROLE_STUDENT -> studentCount++;
      }
    }

    warnOnUnmatchedSuperAdminLogins(superAdminLogins, principals);

    log.info(
        "RBAC assignments seeded: {} student, {} staff, {} super-admin",
        studentCount,
        staffCount,
        superAdminCount);
  }

  private Set<String> parseSuperAdminLogins() {
    if (superAdminLoginsProperty == null || superAdminLoginsProperty.isBlank()) {
      return Set.of();
    }

    return Arrays.stream(superAdminLoginsProperty.split(","))
        .map(String::trim)
        .filter(login -> !login.isEmpty())
        .collect(Collectors.toSet());
  }

  private void warnOnUnmatchedSuperAdminLogins(
      Set<String> superAdminLogins, List<Principal> principals) {
    Set<String> knownLogins =
        principals.stream().map(Principal::getLogin).collect(Collectors.toSet());

    superAdminLogins.stream()
        .filter(login -> !knownLogins.contains(login))
        .forEach(
            login ->
                log.warn(
                    "seeder.rbac.super-admin-logins configures '{}' as ROLE_SUPER_ADMIN, but no"
                        + " matching principal was found",
                    login));
  }

  private void seedAssignment(Principal principal, ERole role) {
    RBACRole rbacRole = roleRepository.findByName(role.name()).orElseThrow();

    boolean alreadyAssigned =
        assignmentRepository.findByPrincipal(principal.getLogin()).stream()
            .anyMatch(assignment -> assignment.role().id().equals(rbacRole.id()));

    if (alreadyAssigned) {
      return;
    }

    assignmentRepository.save(
        new RBACAssignment(
            null,
            principal,
            rbacRole,
            new RBACScope(null, List.of()),
            new RBACContext(null, null, null, Set.of())));
  }

  private ERole resolveRole(Principal principal, Set<String> superAdminLogins) {
    if (superAdminLogins.contains(principal.getLogin())) {
      return ERole.ROLE_SUPER_ADMIN;
    }

    return switch (principal.getCategory()) {
      case STUDENT -> ERole.ROLE_STUDENT;
      case STAFF -> ERole.ROLE_STAFF;
    };
  }
}
