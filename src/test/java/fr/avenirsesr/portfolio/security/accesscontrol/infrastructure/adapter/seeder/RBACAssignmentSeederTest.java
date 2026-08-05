package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RBACAssignmentSeederTest {

  @Mock private PrincipalRepository principalRepository;
  @Mock private RBACRoleRepository roleRepository;
  @Mock private RBACAssignmentRepository assignmentRepository;

  private RBACAssignmentSeeder seeder;

  @BeforeEach
  void setup() {
    seeder = new RBACAssignmentSeeder(principalRepository, roleRepository, assignmentRepository);
  }

  private void givenSuperAdminLogins(String commaSeparatedLogins) {
    ReflectionTestUtils.setField(seeder, "superAdminLoginsProperty", commaSeparatedLogins);
  }

  private Principal principal(String login, EUserCategory category) {
    return Principal.create(login, login, "OIDC", login, category, EUserStatus.ACTIVE, Set.of());
  }

  private RBACRole role(ERole role) {
    return new RBACRole(UUID.randomUUID(), role.name(), role.description(), Set.of());
  }

  @Nested
  class GivenNoConfiguredSuperAdminLogin {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("no configured super-admin login");

      givenSuperAdminLogins("");
    }

    @Test
    void thenAStaffPrincipalShouldBeAssignedRoleStaff() {
      BddLogger.when("seeding a staff principal");
      BddLogger.then("it should be assigned ROLE_STAFF");

      Principal staff = principal("staffLogin", EUserCategory.STAFF);
      when(principalRepository.findAll()).thenReturn(List.of(staff));
      when(roleRepository.findByName(ERole.ROLE_STAFF.name()))
          .thenReturn(java.util.Optional.of(role(ERole.ROLE_STAFF)));
      when(assignmentRepository.findByPrincipal("staffLogin")).thenReturn(List.of());

      seeder.seed();

      verify(assignmentRepository)
          .save(argThat(assignment -> assignment.role().name().equals("ROLE_STAFF")));
    }

    @Test
    void thenAStudentPrincipalShouldBeAssignedRoleStudent() {
      BddLogger.when("seeding a student principal");
      BddLogger.then("it should be assigned ROLE_STUDENT");

      Principal student = principal("studentLogin", EUserCategory.STUDENT);
      when(principalRepository.findAll()).thenReturn(List.of(student));
      when(roleRepository.findByName(ERole.ROLE_STUDENT.name()))
          .thenReturn(java.util.Optional.of(role(ERole.ROLE_STUDENT)));
      when(assignmentRepository.findByPrincipal("studentLogin")).thenReturn(List.of());

      seeder.seed();

      verify(assignmentRepository)
          .save(argThat(assignment -> assignment.role().name().equals("ROLE_STUDENT")));
    }
  }

  @Nested
  class GivenAConfiguredSuperAdminLogin {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a login configured as super-admin");

      givenSuperAdminLogins("admin.login, other.login");
    }

    @Test
    void thenThatPrincipalShouldBeAssignedRoleSuperAdminRegardlessOfCategory() {
      BddLogger.when("seeding a staff principal matching the configured login");
      BddLogger.then("it should be assigned ROLE_SUPER_ADMIN, not ROLE_STAFF");

      Principal configuredStaff = principal("admin.login", EUserCategory.STAFF);
      when(principalRepository.findAll()).thenReturn(List.of(configuredStaff));
      when(roleRepository.findByName(ERole.ROLE_SUPER_ADMIN.name()))
          .thenReturn(java.util.Optional.of(role(ERole.ROLE_SUPER_ADMIN)));
      when(assignmentRepository.findByPrincipal("admin.login")).thenReturn(List.of());

      seeder.seed();

      verify(assignmentRepository)
          .save(argThat(assignment -> assignment.role().name().equals("ROLE_SUPER_ADMIN")));
      verify(roleRepository, never()).findByName(ERole.ROLE_STAFF.name());
    }

    @Test
    void thenAnUnmatchedConfiguredLoginShouldNotFailTheSeeding() {
      BddLogger.when("no principal matches the configured super-admin login");
      BddLogger.then("seeding should still complete without error");

      Principal unrelated = principal("someone.else", EUserCategory.STUDENT);
      when(principalRepository.findAll()).thenReturn(List.of(unrelated));
      when(roleRepository.findByName(ERole.ROLE_STUDENT.name()))
          .thenReturn(java.util.Optional.of(role(ERole.ROLE_STUDENT)));
      when(assignmentRepository.findByPrincipal("someone.else")).thenReturn(List.of());

      seeder.seed();

      verify(assignmentRepository)
          .save(argThat(assignment -> assignment.role().name().equals("ROLE_STUDENT")));
    }
  }

  @Nested
  class GivenThePrincipalIsAlreadyAssignedItsRole {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a principal already assigned its expected role");

      givenSuperAdminLogins("");
    }

    @Test
    void thenSeedingAgainShouldNotCreateADuplicateAssignment() {
      BddLogger.when("seeding runs a second time");
      BddLogger.then("it should not create a duplicate assignment");

      Principal student = principal("studentLogin", EUserCategory.STUDENT);
      RBACRole studentRole = role(ERole.ROLE_STUDENT);

      when(principalRepository.findAll()).thenReturn(List.of(student));
      when(roleRepository.findByName(ERole.ROLE_STUDENT.name()))
          .thenReturn(java.util.Optional.of(studentRole));
      when(assignmentRepository.findByPrincipal("studentLogin"))
          .thenReturn(
              List.of(
                  new RBACAssignment(
                      UUID.randomUUID(),
                      student,
                      studentRole,
                      new RBACScope(null, List.of()),
                      new RBACContext(null, null, null, Set.of()))));

      seeder.seed();

      verify(assignmentRepository, times(0)).save(any());
    }
  }
}
