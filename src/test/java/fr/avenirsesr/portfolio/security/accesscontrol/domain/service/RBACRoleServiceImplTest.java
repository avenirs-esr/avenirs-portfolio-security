package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RBACRoleServiceImplTest {

  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID UNKNOWN_ROLE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000999");

  private static final String ROLE_NAME = "ROLE_OWNER";
  private static final String UNKNOWN_ROLE_NAME = "ROLE_UNKNOWN";
  private static final String LOGIN = "deman";

  @Mock private RBACRoleRepository roleRepository;
  @Mock private RBACAssignmentRepository assignmentRepository;

  private RBACRoleServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACRoleServiceImpl(roleRepository, assignmentRepository);
  }

  @Nested
  class GivenARBACRoleService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC role service");
    }

    @Nested
    class WhenGettingRoleById {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting role by id");
      }

      @Nested
      class AndTheRoleExists {
        private RBACRole role;
        private Optional<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role exists");

          role = role();

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));

          result = service.getRoleById(ROLE_ID);
        }

        @Test
        void thenItShouldReturnRole() {
          BddLogger.then("it should return role");

          assertTrue(result.isPresent());
          assertEquals(role, result.orElseThrow());

          verify(roleRepository).findById(ROLE_ID);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }

      @Nested
      class AndTheRoleDoesNotExist {
        private Optional<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role does not exist");

          when(roleRepository.findById(UNKNOWN_ROLE_ID)).thenReturn(Optional.empty());

          result = service.getRoleById(UNKNOWN_ROLE_ID);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(roleRepository).findById(UNKNOWN_ROLE_ID);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }
    }

    @Nested
    class WhenGettingRoleByName {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting role by name");
      }

      @Nested
      class AndTheRoleExists {
        private RBACRole role;
        private Optional<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role exists");

          role = role();

          when(roleRepository.findByName(ROLE_NAME)).thenReturn(Optional.of(role));

          result = service.getRoleByName(ROLE_NAME);
        }

        @Test
        void thenItShouldReturnRole() {
          BddLogger.then("it should return role");

          assertTrue(result.isPresent());
          assertEquals(role, result.orElseThrow());

          verify(roleRepository).findByName(ROLE_NAME);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }

      @Nested
      class AndTheRoleDoesNotExist {
        private Optional<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role does not exist");

          when(roleRepository.findByName(UNKNOWN_ROLE_NAME)).thenReturn(Optional.empty());

          result = service.getRoleByName(UNKNOWN_ROLE_NAME);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(roleRepository).findByName(UNKNOWN_ROLE_NAME);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }
    }

    @Nested
    class WhenGettingAllRoles {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting all roles");
      }

      @Nested
      class AndTheRepositoryContainsRoles {
        private RBACRole role;
        private List<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository contains roles");

          role = role();

          when(roleRepository.findAll()).thenReturn(List.of(role));

          result = service.getAllRoles();
        }

        @Test
        void thenItShouldReturnRepositoryRoles() {
          BddLogger.then("it should return repository roles");

          assertThat(result).containsExactly(role);

          verify(roleRepository).findAll();
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }

      @Nested
      class AndTheRepositoryIsEmpty {
        private List<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the repository is empty");

          when(roleRepository.findAll()).thenReturn(List.of());

          result = service.getAllRoles();
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(roleRepository).findAll();
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }
    }

    @Nested
    class WhenCreatingRole {
      private RBACRole roleToCreate;
      private RBACRole savedRole;
      private RBACRole result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating role");

        roleToCreate = roleWithoutId();
        savedRole = role();

        when(roleRepository.save(roleToCreate)).thenReturn(savedRole);

        result = service.createRole(roleToCreate);
      }

      @Test
      void thenItShouldSaveRole() {
        BddLogger.then("it should save role");

        assertEquals(savedRole, result);
        assertEquals(ROLE_ID, result.id());

        verify(roleRepository).save(roleToCreate);
        verifyNoMoreInteractions(roleRepository, assignmentRepository);
      }
    }

    @Nested
    class WhenUpdatingRole {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating role");
      }

      @Nested
      class AndTheRoleExists {
        private RBACRole role;
        private RBACRole result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role exists");

          role = role();

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(roleRepository.save(role)).thenReturn(role);

          result = service.updateRole(role);
        }

        @Test
        void thenItShouldSaveRole() {
          BddLogger.then("it should save role");

          assertEquals(role, result);

          verify(roleRepository).findById(ROLE_ID);
          verify(roleRepository).save(role);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }

      @Nested
      class AndTheRoleDoesNotExist {
        private RBACRole role;
        private AccessControlNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role does not exist");

          role = role();

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

          exception =
              assertThrows(AccessControlNotFoundException.class, () -> service.updateRole(role));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertEquals(
              "Role not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

          verify(roleRepository).findById(ROLE_ID);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }
    }

    @Nested
    class WhenDeletingRole {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting role");

        service.deleteRole(ROLE_ID);
      }

      @Test
      void thenItShouldDeleteById() {
        BddLogger.then("it should delete by id");

        verify(roleRepository).deleteById(ROLE_ID);
        verifyNoMoreInteractions(roleRepository, assignmentRepository);
      }
    }

    @Nested
    class WhenGettingRolesByPrincipalLogin {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting roles by principal login");
      }

      @Nested
      class AndThePrincipalHasAssignments {
        private RBACRole owner;
        private RBACRole pair;
        private List<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal has assignments");

          owner = role();
          pair =
              new RBACRole(
                  UUID.fromString("00000000-0000-0000-0000-000000000002"),
                  "ROLE_PAIR",
                  "Can give feedback",
                  Set.of(permission("PERM_READ"), permission("PERM_COMMENT")));

          when(assignmentRepository.findByPrincipal(LOGIN))
              .thenReturn(List.of(assignment(owner), assignment(owner), assignment(pair)));

          result = service.getRolesByPrincipalLogin(LOGIN);
        }

        @Test
        void thenItShouldReturnDistinctRolesFromAssignments() {
          BddLogger.then("it should return distinct roles from assignments");

          assertThat(result).containsExactly(owner, pair);

          verify(assignmentRepository).findByPrincipal(LOGIN);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }

      @Nested
      class AndThePrincipalHasNoAssignment {
        private List<RBACRole> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal has no assignment");

          when(assignmentRepository.findByPrincipal(LOGIN)).thenReturn(List.of());

          result = service.getRolesByPrincipalLogin(LOGIN);
        }

        @Test
        void thenItShouldReturnEmptyList() {
          BddLogger.then("it should return an empty list");

          assertThat(result).isEmpty();

          verify(assignmentRepository).findByPrincipal(LOGIN);
          verifyNoMoreInteractions(roleRepository, assignmentRepository);
        }
      }
    }
  }

  private RBACRole role() {
    return new RBACRole(
        ROLE_ID,
        ROLE_NAME,
        "Owner of the resource",
        Set.of(
            permission("PERM_READ"),
            permission("PERM_WRITE"),
            permission("PERM_COMMENT"),
            permission("PERM_SHARE"),
            permission("PERM_DELETE")));
  }

  private RBACRole roleWithoutId() {
    return new RBACRole(null, ROLE_NAME, "Owner of the resource", Set.of(permission("PERM_READ")));
  }

  private RBACAssignment assignment(RBACRole role) {
    Principal principal =
        Principal.create(
            "user@university.com",
            LOGIN,
            "OIDC",
            LOGIN,
            Set.of(EUserCategory.STUDENT),
            EUserStatus.ACTIVE,
            Set.of());
    return new RBACAssignment(
        UUID.randomUUID(),
        principal,
        role,
        new RBACScope(UUID.randomUUID(), List.of()),
        new RBACContext(UUID.randomUUID(), null, null, Set.of()));
  }

  private RBACPermission permission(String name) {
    return new RBACPermission(UUID.randomUUID(), name, "");
  }
}
