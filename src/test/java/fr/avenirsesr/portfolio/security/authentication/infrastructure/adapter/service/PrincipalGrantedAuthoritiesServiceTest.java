package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class PrincipalGrantedAuthoritiesServiceTest {

  private static final String LOGIN = "superadmin";

  @Mock private RBACAssignmentRepository assignmentRepository;

  private PrincipalGrantedAuthoritiesService service;

  @BeforeEach
  void setup() {
    service = new PrincipalGrantedAuthoritiesService(assignmentRepository);
  }

  @Nested
  class GivenAPrincipalLogin {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a principal login");
    }

    @Nested
    class WhenThePrincipalHasNoAssignment {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("the principal has no assignment at all");

        when(assignmentRepository.findByPrincipal(LOGIN)).thenReturn(List.of());
      }

      @Test
      void thenItShouldReturnNoAuthority() {
        BddLogger.then("it should return an empty authority set");

        assertThat(service.loadAuthorities(LOGIN)).isEmpty();
      }

      @Test
      void thenItShouldReturnNoRole() {
        BddLogger.then("it should return an empty role set");

        assertThat(service.resolveRoles(LOGIN)).isEmpty();
      }
    }

    @Nested
    class WhenThePrincipalHasAnActiveSuperAdminAssignment {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("the principal has an active assignment to ROLE_SUPER_ADMIN");

        when(assignmentRepository.findByPrincipal(LOGIN))
            .thenReturn(List.of(assignment(role(ERole.ROLE_SUPER_ADMIN), noValidityContext())));
      }

      @Test
      void thenItShouldReturnTheFourRbacAdministrationAuthorities() {
        BddLogger.then("it should return the four rbac:* authorities");

        Set<String> authorityStrings = authorityStrings(service.loadAuthorities(LOGIN));

        assertThat(authorityStrings)
            .contains("rbac:read", "rbac:assign", "rbac:revoke", "rbac:manage");
      }

      @Test
      void thenItShouldReturnTheSuperAdminRole() {
        BddLogger.then("it should return the ROLE_SUPER_ADMIN role");

        assertThat(service.resolveRoles(LOGIN)).containsExactly(ERole.ROLE_SUPER_ADMIN.name());
      }
    }

    @Nested
    class WhenThePrincipalHasAStudentOrStaffAssignment {

      @Test
      void thenItShouldNotReturnAnyRbacAdministrationAuthority() {
        BddLogger.when("the principal has an active assignment to ROLE_STUDENT or ROLE_STAFF");
        BddLogger.then("it should not return any rbac:* authority");

        when(assignmentRepository.findByPrincipal(LOGIN))
            .thenReturn(
                List.of(
                    assignment(role(ERole.ROLE_STUDENT), noValidityContext()),
                    assignment(role(ERole.ROLE_STAFF), noValidityContext())));

        Set<String> authorityStrings = authorityStrings(service.loadAuthorities(LOGIN));

        assertThat(authorityStrings).noneMatch(authority -> authority.startsWith("rbac:"));
      }
    }

    @Nested
    class WhenTheAssignmentIsExpired {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("the assignment's validity end is in the past");

        RBACContext expired =
            new RBACContext(UUID.randomUUID(), null, LocalDateTime.now().minusDays(1), Set.of());

        when(assignmentRepository.findByPrincipal(LOGIN))
            .thenReturn(List.of(assignment(role(ERole.ROLE_SUPER_ADMIN), expired)));
      }

      @Test
      void thenItShouldNotGrantItsPermissions() {
        BddLogger.then("it should not grant the role's permissions");

        assertThat(service.loadAuthorities(LOGIN)).isEmpty();
      }

      @Test
      void thenItShouldNotResolveItsRole() {
        BddLogger.then("it should not resolve the expired assignment's role");

        assertThat(service.resolveRoles(LOGIN)).isEmpty();
      }
    }

    @Nested
    class WhenTheAssignmentIsNotYetValid {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("the assignment's validity start is in the future");

        RBACContext notYetValid =
            new RBACContext(UUID.randomUUID(), LocalDateTime.now().plusDays(1), null, Set.of());

        when(assignmentRepository.findByPrincipal(LOGIN))
            .thenReturn(List.of(assignment(role(ERole.ROLE_SUPER_ADMIN), notYetValid)));
      }

      @Test
      void thenItShouldNotGrantItsPermissions() {
        BddLogger.then("it should not grant the role's permissions");

        assertThat(service.loadAuthorities(LOGIN)).isEmpty();
      }

      @Test
      void thenItShouldNotResolveItsRole() {
        BddLogger.then("it should not resolve the not-yet-valid assignment's role");

        assertThat(service.resolveRoles(LOGIN)).isEmpty();
      }
    }

    @Nested
    class WhenSeveralAssignmentsGrantOverlappingPermissions {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("several active assignments grant the same permission");

        RBACRole role = role(ERole.ROLE_SUPER_ADMIN);

        when(assignmentRepository.findByPrincipal(LOGIN))
            .thenReturn(
                List.of(
                    assignment(role, noValidityContext()), assignment(role, noValidityContext())));
      }

      @Test
      void thenItShouldNotDuplicateAuthorities() {
        BddLogger.then("it should not contain duplicate authorities");

        Set<GrantedAuthority> authorities = service.loadAuthorities(LOGIN);

        assertThat(authorities).hasSize(ERole.ROLE_SUPER_ADMIN.permissions().size());
      }

      @Test
      void thenItShouldNotDuplicateRoles() {
        BddLogger.then("it should not contain duplicate roles");

        assertThat(service.resolveRoles(LOGIN)).containsExactly(ERole.ROLE_SUPER_ADMIN.name());
      }
    }
  }

  private RBACRole role(ERole role) {
    Set<RBACPermission> permissions =
        role.permissions().stream()
            .map(
                permission ->
                    new RBACPermission(
                        UUID.randomUUID(), permission.authority(), permission.description()))
            .collect(Collectors.toSet());

    return new RBACRole(UUID.randomUUID(), role.name(), role.description(), permissions);
  }

  private RBACContext noValidityContext() {
    return new RBACContext(UUID.randomUUID(), null, null, Set.of());
  }

  private RBACAssignment assignment(RBACRole role, RBACContext context) {
    return new RBACAssignment(UUID.randomUUID(), null, role, null, context);
  }

  private Set<String> authorityStrings(Set<GrantedAuthority> authorities) {
    return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
  }
}
