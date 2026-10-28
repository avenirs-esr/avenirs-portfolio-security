package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  private Principal principal(String login, EUserCategory... categories) {
    return Principal.create(
        login, login, "OIDC", login, Set.of(categories), EUserStatus.ACTIVE, Set.of());
  }

  private final Map<ERole, RBACRole> rolesByEnum = new HashMap<>();

  private RBACRole role(ERole role) {
    return rolesByEnum.computeIfAbsent(
        role, r -> new RBACRole(UUID.randomUUID(), r.name(), r.description(), Set.of()));
  }

  private void givenAllRolesExist() {
    when(roleRepository.findByName(anyString()))
        .thenAnswer(
            invocation -> Optional.of(role(ERole.valueOf(invocation.<String>getArgument(0)))));
  }

  private void verifyAssigned(ERole... roles) {
    for (ERole role : roles) {
      verify(assignmentRepository)
          .save(argThat(assignment -> assignment.role().name().equals(role.name())));
    }
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
      BddLogger.then("it should be assigned ROLE_STAFF and each of its included roles");

      Principal staff = principal("staffLogin", EUserCategory.STAFF);
      when(principalRepository.findAll()).thenReturn(List.of(staff));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("staffLogin")).thenReturn(List.of());

      seeder.seed();

      verifyAssigned(
          ERole.ROLE_STAFF,
          ERole.ROLE_USER_BASE,
          ERole.ROLE_STAFF_ACTIVITY_AUTHOR,
          ERole.ROLE_ACTIVITY_EDITOR);
    }

    @Test
    void thenAStudentPrincipalShouldBeAssignedRoleStudent() {
      BddLogger.when("seeding a student principal");
      BddLogger.then("it should be assigned ROLE_STUDENT and each of its included roles");

      Principal student = principal("studentLogin", EUserCategory.STUDENT);
      when(principalRepository.findAll()).thenReturn(List.of(student));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("studentLogin")).thenReturn(List.of());

      seeder.seed();

      verifyAssigned(
          ERole.ROLE_STUDENT,
          ERole.ROLE_USER_BASE,
          ERole.ROLE_STUDENT_PORTFOLIO,
          ERole.ROLE_STUDENT_REFERENCE_DATA);
    }

    @Test
    void thenAPrincipalWithBothCategoriesShouldBeAssignedBothRoles() {
      BddLogger.when("seeding a principal that is both a student and a staff member");
      BddLogger.then("it should be assigned ROLE_STUDENT and ROLE_STAFF");

      Principal both = principal("bothLogin", EUserCategory.STUDENT, EUserCategory.STAFF);
      when(principalRepository.findAll()).thenReturn(List.of(both));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("bothLogin")).thenReturn(List.of());

      seeder.seed();

      verifyAssigned(ERole.ROLE_STUDENT, ERole.ROLE_STAFF);
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
    void thenThatPrincipalShouldBeAssignedRoleSuperAdminAndItsCategoryRole() {
      BddLogger.when("seeding a staff principal matching the configured login");
      BddLogger.then(
          "it should be assigned ROLE_SUPER_ADMIN, its nested roles such as ROLE_GROUP_MANAGER,"
              + " and ROLE_STAFF");

      Principal configuredStaff = principal("admin.login", EUserCategory.STAFF);
      when(principalRepository.findAll()).thenReturn(List.of(configuredStaff));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("admin.login")).thenReturn(List.of());

      seeder.seed();

      verifyAssigned(
          ERole.ROLE_SUPER_ADMIN,
          ERole.ROLE_ADMIN,
          ERole.ROLE_GROUP_MANAGER,
          ERole.ROLE_RBAC_ADMIN,
          ERole.ROLE_STAFF);
    }

    @Test
    void thenAnUnmatchedConfiguredLoginShouldNotFailTheSeeding() {
      BddLogger.when("no principal matches the configured super-admin login");
      BddLogger.then("seeding should still complete without error");

      Principal unrelated = principal("someone.else", EUserCategory.STUDENT);
      when(principalRepository.findAll()).thenReturn(List.of(unrelated));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("someone.else")).thenReturn(List.of());

      seeder.seed();

      verifyAssigned(ERole.ROLE_STUDENT);
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
      Set<ERole> studentRoles =
          Set.of(
              ERole.ROLE_STUDENT,
              ERole.ROLE_USER_BASE,
              ERole.ROLE_STUDENT_PORTFOLIO,
              ERole.ROLE_STUDENT_ACTIVITY,
              ERole.ROLE_STUDENT_FEEDBACK,
              ERole.ROLE_STUDENT_EMPLOYMENT_KIT,
              ERole.ROLE_STUDENT_REFERENCE_DATA);

      when(principalRepository.findAll()).thenReturn(List.of(student));
      givenAllRolesExist();
      when(assignmentRepository.findByPrincipal("studentLogin"))
          .thenReturn(
              studentRoles.stream()
                  .map(
                      role ->
                          new RBACAssignment(
                              UUID.randomUUID(),
                              student,
                              role(role),
                              new RBACScope(null, List.of()),
                              new RBACContext(null, null, null, Set.of())))
                  .toList());

      seeder.seed();

      verify(assignmentRepository, times(0)).save(any());
    }
  }
}
