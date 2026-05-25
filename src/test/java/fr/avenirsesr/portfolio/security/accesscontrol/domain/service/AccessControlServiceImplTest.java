package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlInvalidDateException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.*;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceImplTest {

  private static final String DATE_FORMAT = "yyyy-MM-dd";

  private static final UUID PRINCIPAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
  private static final UUID ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID RESOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  private static final UUID RESOURCE_ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000004");
  private static final UUID STRUCTURE_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
  private static final UUID ACTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
  private static final UUID ASSIGNMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");

  private static final String LOGIN = "gribonvald";

  @Mock private RBACActionRepository actionRepository;
  @Mock private RBACAssignmentRepository assignmentRepository;
  @Mock private PrincipalRepository principalRepository;
  @Mock private RBACResourceRepository resourceRepository;
  @Mock private RBACRoleRepository roleRepository;
  @Mock private StructureRepository structureRepository;

  private AccessControlServiceImpl service;

  private Principal principal;
  private RBACRole role;
  private RBACPermission readPermission;
  private RBACPermission writePermission;
  private RBACResource resource;
  private RBACResource resource2;
  private Structure structure;

  @BeforeEach
  void setUp() {
    service =
        new AccessControlServiceImpl(
            actionRepository,
            assignmentRepository,
            principalRepository,
            resourceRepository,
            roleRepository,
            structureRepository,
            DATE_FORMAT);

    ReflectionTestUtils.setField(service, "dateFormat", DATE_FORMAT);

    structure = new Structure(STRUCTURE_ID, "RECIA", "Structure RECIA");
    principal =
        Principal.toDomain(
            PRINCIPAL_ID,
            Instant.now(),
            Instant.now(),
            "user@university.com",
            LOGIN,
            "OIDC",
            LOGIN,
            EUserCategory.STUDENT,
            EUserStatus.ACTIVE,
            Set.of(structure));

    readPermission =
        new RBACPermission(
            UUID.fromString("00000000-0000-0000-0000-000000000010"),
            "PERM_READ",
            "Read permission");

    writePermission =
        new RBACPermission(
            UUID.fromString("00000000-0000-0000-0000-000000000011"),
            "PERM_WRITE",
            "Write permission");

    role = new RBACRole(ROLE_ID, "ROLE_OWNER", "Owner", Set.of(readPermission, writePermission));

    RBACResourceType resourceType =
        new RBACResourceType(
            UUID.fromString("00000000-0000-0000-0000-000000000020"),
            "PORTFOLIO",
            "Portfolio resource type");

    resource = new RBACResource(RESOURCE_ID, "ptf_0000", resourceType);
    resource2 = new RBACResource(RESOURCE_ID_2, "ptf_0001", resourceType);
  }

  @Nested
  class GivenAccessControlService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an access control service");
    }

    @Nested
    class WhenGrantingAccess {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("granting access");
      }

      @Nested
      class AndTheCommandIsValid {
        private AccessControlGrantResult result;
        private ArgumentCaptor<RBACAssignment> assignmentCaptor;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the command is valid");

          AccessControlGrantCommand command =
              grantCommand(
                  List.of(RESOURCE_ID, RESOURCE_ID_2),
                  "2024-10-01",
                  "2024-12-31",
                  List.of(STRUCTURE_ID));

          mockValidGrantDependencies();
          assignmentCaptor = ArgumentCaptor.forClass(RBACAssignment.class);

          when(assignmentRepository.save(any(RBACAssignment.class)))
              .thenAnswer(this::savedAssignment);

          result = service.grantAccess(command);
        }

        @Test
        void thenItShouldCreateAssignment() {
          BddLogger.then("it should create the assignment");

          assertEquals(new AccessControlGrantResult(LOGIN, true, ASSIGNMENT_ID, null), result);

          verify(assignmentRepository).save(assignmentCaptor.capture());

          RBACAssignment savedAssignment = assignmentCaptor.getValue();

          assertNull(savedAssignment.id());
          assertEquals(principal, savedAssignment.principal());
          assertEquals(role, savedAssignment.role());

          assertThat(savedAssignment.scope().resources())
              .extracting(RBACResource::id)
              .containsExactlyInAnyOrder(RESOURCE_ID, RESOURCE_ID_2);

          assertEquals(
              LocalDateTime.of(2024, 10, 1, 0, 0), savedAssignment.context().validityStart());
          assertEquals(
              LocalDateTime.of(2024, 12, 31, 0, 0), savedAssignment.context().validityEnd());

          assertThat(savedAssignment.context().structures())
              .extracting(Structure::id)
              .containsExactly(STRUCTURE_ID);
        }

        private RBACAssignment savedAssignment(InvocationOnMock invocation) {
          RBACAssignment assignment = invocation.getArgument(0);
          return new RBACAssignment(
              ASSIGNMENT_ID,
              assignment.principal(),
              assignment.role(),
              assignment.scope(),
              assignment.context());
        }
      }

      @Nested
      class AndDatesAreBlank {
        private AccessControlGrantResult result;
        private ArgumentCaptor<RBACAssignment> assignmentCaptor;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("dates are blank");

          AccessControlGrantCommand command =
              new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "", "   ", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.save(any(RBACAssignment.class)))
              .thenAnswer(this::savedAssignment);

          assignmentCaptor = ArgumentCaptor.forClass(RBACAssignment.class);

          result = service.grantAccess(command);
        }

        @Test
        void thenItShouldCreateAssignmentWithNullValidityDates() {
          BddLogger.then("it should create assignment with null validity dates");

          assertTrue(result.granted());

          verify(assignmentRepository).save(assignmentCaptor.capture());

          assertNull(assignmentCaptor.getValue().context().validityStart());
          assertNull(assignmentCaptor.getValue().context().validityEnd());
          assertThat(assignmentCaptor.getValue().scope().resources()).isEmpty();
          assertThat(assignmentCaptor.getValue().context().structures()).isEmpty();
        }

        private RBACAssignment savedAssignment(InvocationOnMock invocation) {
          RBACAssignment assignment = invocation.getArgument(0);
          return new RBACAssignment(
              ASSIGNMENT_ID,
              assignment.principal(),
              assignment.role(),
              assignment.scope(),
              assignment.context());
        }
      }

      @Nested
      class AndTheRoleDoesNotExist {
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the role does not exist");

          command = grantCommand(List.of(RESOURCE_ID), "2024-10-01", "2024-12-31", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlNotFoundException.class)
              .hasMessage("Role not found, ID: " + ROLE_ID);

          verifyNoInteractions(
              principalRepository, resourceRepository, structureRepository, assignmentRepository);
        }
      }

      @Nested
      class AndThePrincipalDoesNotExist {
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the principal does not exist");

          command = grantCommand(List.of(RESOURCE_ID), "2024-10-01", "2024-12-31", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlNotFoundException.class)
              .hasMessage("Principal not found, login: " + LOGIN);

          verifyNoInteractions(resourceRepository, structureRepository, assignmentRepository);
        }
      }

      @Nested
      class AndAResourceIsMissing {
        private UUID missingResourceId;
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a resource is missing");

          missingResourceId = UUID.fromString("00000000-0000-0000-0000-000000000099");
          command =
              grantCommand(
                  List.of(RESOURCE_ID, missingResourceId), "2024-10-01", "2024-12-31", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(resourceRepository.findAllByIds(List.of(RESOURCE_ID, missingResourceId)))
              .thenReturn(List.of(resource));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlNotFoundException.class)
              .hasMessage("Missing resources, IDs: [" + missingResourceId + "]");

          verifyNoInteractions(structureRepository, assignmentRepository);
        }
      }

      @Nested
      class AndAStructureIsMissing {
        private UUID missingStructureId;
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a structure is missing");

          missingStructureId = UUID.fromString("00000000-0000-0000-0000-000000000099");
          command =
              grantCommand(
                  List.of(RESOURCE_ID),
                  "2024-10-01",
                  "2024-12-31",
                  List.of(STRUCTURE_ID, missingStructureId));

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(resourceRepository.findAllByIds(List.of(RESOURCE_ID))).thenReturn(List.of(resource));
          when(structureRepository.findAllByIds(List.of(STRUCTURE_ID, missingStructureId)))
              .thenReturn(List.of(structure));
        }

        @Test
        void thenItShouldThrowNotFoundException() {
          BddLogger.then("it should throw a not found exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlNotFoundException.class)
              .hasMessage("Missing structures, IDs: [" + missingStructureId + "]");

          verifyNoInteractions(assignmentRepository);
        }
      }

      @Nested
      class AndValidityStartFormatIsInvalid {
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("validity start format is invalid");

          command =
              new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "01/10/2024", "2024-12-31", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
        }

        @Test
        void thenItShouldThrowInvalidDateException() {
          BddLogger.then("it should throw an invalid date exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlInvalidDateException.class)
              .hasMessageContaining("Invalid date format");

          verifyNoInteractions(assignmentRepository);
        }
      }

      @Nested
      class AndValidityEndFormatIsInvalid {
        private AccessControlGrantCommand command;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("validity end format is invalid");

          command =
              new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "2024-10-01", "31/12/2024", null);

          when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
        }

        @Test
        void thenItShouldThrowInvalidDateException() {
          BddLogger.then("it should throw an invalid date exception");

          assertThatThrownBy(() -> service.grantAccess(command))
              .isInstanceOf(AccessControlInvalidDateException.class)
              .hasMessageContaining("Invalid date format");

          verifyNoInteractions(assignmentRepository);
        }
      }
    }

    @Nested
    class WhenRevokingAccess {
      private AccessControlRevokeResult result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("revoking access");

        result = service.revokeAccess(new AccessControlRevokeCommand(LOGIN, ASSIGNMENT_ID));
      }

      @Test
      void thenItShouldDeleteAssignmentAndReturnResult() {
        BddLogger.then("it should delete assignment and return result");

        assertEquals(new AccessControlRevokeResult(LOGIN, true, ASSIGNMENT_ID, null), result);

        verify(assignmentRepository).deleteById(ASSIGNMENT_ID);
      }
    }

    @Nested
    class WhenCheckingAuthorization {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("checking authorization");
      }

      @Nested
      class AndPrincipalDoesNotExist {
        private boolean authorized;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("principal does not exist");

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

          authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnFalse() {
          BddLogger.then("it should return false");

          assertFalse(authorized);

          verify(principalRepository).findByLogin(LOGIN);
          verifyNoInteractions(assignmentRepository, actionRepository);
        }
      }

      @Nested
      class AndPrincipalHasNoAssignmentPermission {
        private boolean authorized;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("principal has no assignment permission");

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.findByPrincipalContextAndResource(
                  eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
              .thenReturn(List.of());

          authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnFalse() {
          BddLogger.then("it should return false");

          assertFalse(authorized);

          verifyNoInteractions(actionRepository);
        }
      }

      @Nested
      class AndActionDoesNotExist {
        private boolean authorized;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("action does not exist");

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.findByPrincipalContextAndResource(
                  eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
              .thenReturn(List.of(assignment(role)));
          when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.empty());

          authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnFalse() {
          BddLogger.then("it should return false");

          assertFalse(authorized);
        }
      }

      @Nested
      class AndPrincipalDoesNotHaveAllRequiredPermissions {
        private boolean authorized;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("principal does not have all required permissions");

          RBACAction action =
              new RBACAction(
                  ACTION_ID, "ACT_EDIT", "Edit action", List.of(readPermission, writePermission));

          RBACRole readOnlyRole =
              new RBACRole(ROLE_ID, "ROLE_READER", "Reader", Set.of(readPermission));

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.findByPrincipalContextAndResource(
                  eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
              .thenReturn(List.of(assignment(readOnlyRole)));
          when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));

          authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnFalse() {
          BddLogger.then("it should return false");

          assertFalse(authorized);
        }
      }

      @Nested
      class AndPrincipalHasAllRequiredPermissions {
        private boolean authorized;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("principal has all required permissions");

          RBACAction action =
              new RBACAction(
                  ACTION_ID, "ACT_EDIT", "Edit action", List.of(readPermission, writePermission));

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.findByPrincipalContextAndResource(
                  eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
              .thenReturn(List.of(assignment(role)));
          when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));

          authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);
        }

        @Test
        void thenItShouldReturnTrue() {
          BddLogger.then("it should return true");

          assertTrue(authorized);
        }
      }

      @Nested
      class AndActionPermissionsAreCached {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("action permissions are cached");

          RBACAction action =
              new RBACAction(ACTION_ID, "ACT_DISPLAY", "Display action", List.of(readPermission));

          when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
          when(assignmentRepository.findByPrincipalContextAndResource(
                  eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
              .thenReturn(List.of(assignment(role)));
          when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));
        }

        @Test
        void thenItShouldUseRepositoryOnlyOnceForTheSameAction() {
          BddLogger.then("it should use repository only once for the same action");

          assertTrue(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID));
          assertTrue(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID));

          verify(actionRepository, times(1)).findById(ACTION_ID);
        }
      }
    }
  }

  private AccessControlGrantCommand grantCommand(
      List<UUID> resourceIds, String validityStart, String validityEnd, List<UUID> structureIds) {
    return new AccessControlGrantCommand(
        LOGIN, ROLE_ID, resourceIds, validityStart, validityEnd, structureIds);
  }

  private void mockValidGrantDependencies() {
    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(resourceRepository.findAllByIds(List.of(RESOURCE_ID, RESOURCE_ID_2)))
        .thenReturn(List.of(resource, resource2));
    when(structureRepository.findAllByIds(List.of(STRUCTURE_ID))).thenReturn(List.of(structure));
  }

  private RBACAssignment assignment(RBACRole role) {
    return new RBACAssignment(
        ASSIGNMENT_ID,
        principal,
        role,
        new RBACScope(null, List.of(resource)),
        new RBACContext(null, null, null, Set.of(structure)));
  }
}
