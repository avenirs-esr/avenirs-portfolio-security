package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlInvalidDateException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.*;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.*;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.StructureRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
            "yyyy-MM-dd");

    ReflectionTestUtils.setField(service, "dateFormat", DATE_FORMAT);

    structure = new Structure(STRUCTURE_ID, "RECIA", "Structure RECIA");
    principal = new Principal(PRINCIPAL_ID, LOGIN, "OIDC", LOGIN, USER_ID, Set.of(structure));

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

  @Test
  void grantAccessCreatesAssignment() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(
            LOGIN,
            ROLE_ID,
            List.of(RESOURCE_ID, RESOURCE_ID_2),
            "2024-10-01",
            "2024-12-31",
            List.of(STRUCTURE_ID));

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(resourceRepository.findAllByIds(List.of(RESOURCE_ID, RESOURCE_ID_2)))
        .thenReturn(List.of(resource, resource2));
    when(structureRepository.findAllByIds(List.of(STRUCTURE_ID))).thenReturn(List.of(structure));

    ArgumentCaptor<RBACAssignment> assignmentCaptor = ArgumentCaptor.forClass(RBACAssignment.class);

    when(assignmentRepository.save(any(RBACAssignment.class)))
        .thenAnswer(
            invocation -> {
              RBACAssignment assignment = invocation.getArgument(0);
              return new RBACAssignment(
                  ASSIGNMENT_ID,
                  assignment.principal(),
                  assignment.role(),
                  assignment.scope(),
                  assignment.context());
            });

    AccessControlGrantResult result = service.grantAccess(command);

    assertEquals(new AccessControlGrantResult(LOGIN, true, ASSIGNMENT_ID, null), result);

    verify(assignmentRepository).save(assignmentCaptor.capture());

    RBACAssignment savedAssignment = assignmentCaptor.getValue();

    assertNull(savedAssignment.id());
    assertEquals(principal, savedAssignment.principal());
    assertEquals(role, savedAssignment.role());

    assertThat(savedAssignment.scope().resources())
        .extracting(RBACResource::id)
        .containsExactlyInAnyOrder(RESOURCE_ID, RESOURCE_ID_2);

    assertEquals(LocalDateTime.of(2024, 10, 1, 0, 0), savedAssignment.context().validityStart());
    assertEquals(LocalDateTime.of(2024, 12, 31, 0, 0), savedAssignment.context().validityEnd());

    assertThat(savedAssignment.context().structures())
        .extracting(Structure::id)
        .containsExactly(STRUCTURE_ID);
  }

  @Test
  void grantAccessWithBlankDatesCreatesNullValidityDates() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "", "   ", null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.save(any(RBACAssignment.class)))
        .thenAnswer(
            invocation -> {
              RBACAssignment assignment = invocation.getArgument(0);
              return new RBACAssignment(
                  ASSIGNMENT_ID,
                  assignment.principal(),
                  assignment.role(),
                  assignment.scope(),
                  assignment.context());
            });

    AccessControlGrantResult result = service.grantAccess(command);

    assertTrue(result.granted());

    ArgumentCaptor<RBACAssignment> captor = ArgumentCaptor.forClass(RBACAssignment.class);
    verify(assignmentRepository).save(captor.capture());

    assertNull(captor.getValue().context().validityStart());
    assertNull(captor.getValue().context().validityEnd());
    assertThat(captor.getValue().scope().resources()).isEmpty();
    assertThat(captor.getValue().context().structures()).isEmpty();
  }

  @Test
  void grantAccessThrowsWhenRoleDoesNotExist() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(
            LOGIN, ROLE_ID, List.of(RESOURCE_ID), "2024-10-01", "2024-12-31", null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlNotFoundException.class)
        .hasMessage("Role not found, ID: " + ROLE_ID);

    verifyNoInteractions(
        principalRepository, resourceRepository, structureRepository, assignmentRepository);
  }

  @Test
  void grantAccessThrowsWhenPrincipalDoesNotExist() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(
            LOGIN, ROLE_ID, List.of(RESOURCE_ID), "2024-10-01", "2024-12-31", null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlNotFoundException.class)
        .hasMessage("Principal not found, login: " + LOGIN);

    verifyNoInteractions(resourceRepository, structureRepository, assignmentRepository);
  }

  @Test
  void grantAccessThrowsWhenResourceIsMissing() {
    UUID missingResourceId = UUID.fromString("00000000-0000-0000-0000-000000000099");

    AccessControlGrantCommand command =
        new AccessControlGrantCommand(
            LOGIN,
            ROLE_ID,
            List.of(RESOURCE_ID, missingResourceId),
            "2024-10-01",
            "2024-12-31",
            null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(resourceRepository.findAllByIds(List.of(RESOURCE_ID, missingResourceId)))
        .thenReturn(List.of(resource));

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlNotFoundException.class)
        .hasMessage("Missing resources, IDs: [" + missingResourceId + "]");

    verifyNoInteractions(structureRepository, assignmentRepository);
  }

  @Test
  void grantAccessThrowsWhenStructureIsMissing() {
    UUID missingStructureId = UUID.fromString("00000000-0000-0000-0000-000000000099");

    AccessControlGrantCommand command =
        new AccessControlGrantCommand(
            LOGIN,
            ROLE_ID,
            List.of(RESOURCE_ID),
            "2024-10-01",
            "2024-12-31",
            List.of(STRUCTURE_ID, missingStructureId));

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(resourceRepository.findAllByIds(List.of(RESOURCE_ID))).thenReturn(List.of(resource));
    when(structureRepository.findAllByIds(List.of(STRUCTURE_ID, missingStructureId)))
        .thenReturn(List.of(structure));

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlNotFoundException.class)
        .hasMessage("Missing structures, IDs: [" + missingStructureId + "]");

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void grantAccessThrowsWhenValidityStartFormatIsInvalid() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "01/10/2024", "2024-12-31", null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlInvalidDateException.class)
        .hasMessageContaining("Invalid date format");

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void grantAccessThrowsWhenValidityEndFormatIsInvalid() {
    AccessControlGrantCommand command =
        new AccessControlGrantCommand(LOGIN, ROLE_ID, null, "2024-10-01", "31/12/2024", null);

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));

    assertThatThrownBy(() -> service.grantAccess(command))
        .isInstanceOf(AccessControlInvalidDateException.class)
        .hasMessageContaining("Invalid date format");

    verifyNoInteractions(assignmentRepository);
  }

  @Test
  void revokeAccessDeletesAssignmentAndReturnsResult() {
    AccessControlRevokeCommand command = new AccessControlRevokeCommand(LOGIN, ASSIGNMENT_ID);

    AccessControlRevokeResult result = service.revokeAccess(command);

    assertEquals(new AccessControlRevokeResult(LOGIN, true, ASSIGNMENT_ID, null), result);

    verify(assignmentRepository).deleteById(ASSIGNMENT_ID);
  }

  @Test
  void isAuthorizedReturnsFalseWhenPrincipalDoesNotExist() {
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());

    boolean authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertFalse(authorized);

    verify(principalRepository).findByLogin(LOGIN);
    verifyNoInteractions(assignmentRepository, actionRepository);
  }

  @Test
  void isAuthorizedReturnsFalseWhenPrincipalHasNoAssignmentPermission() {
    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.findByPrincipalContextAndResource(
            eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
        .thenReturn(List.of());

    boolean authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertFalse(authorized);

    verifyNoInteractions(actionRepository);
  }

  @Test
  void isAuthorizedReturnsFalseWhenActionDoesNotExist() {
    RBACAssignment assignment =
        new RBACAssignment(
            ASSIGNMENT_ID,
            principal,
            role,
            new RBACScope(null, List.of(resource)),
            new RBACContext(null, null, null, Set.of(structure)));

    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.findByPrincipalContextAndResource(
            eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
        .thenReturn(List.of(assignment));
    when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.empty());

    boolean authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertFalse(authorized);
  }

  @Test
  void isAuthorizedReturnsFalseWhenPrincipalDoesNotHaveAllRequiredPermissions() {
    RBACAction action =
        new RBACAction(
            ACTION_ID, "ACT_EDIT", "Edit action", List.of(readPermission, writePermission));

    RBACRole readOnlyRole = new RBACRole(ROLE_ID, "ROLE_READER", "Reader", Set.of(readPermission));

    RBACAssignment assignment =
        new RBACAssignment(
            ASSIGNMENT_ID,
            principal,
            readOnlyRole,
            new RBACScope(null, List.of(resource)),
            new RBACContext(null, null, null, Set.of(structure)));

    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.findByPrincipalContextAndResource(
            eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
        .thenReturn(List.of(assignment));
    when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));

    boolean authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertFalse(authorized);
  }

  @Test
  void isAuthorizedReturnsTrueWhenPrincipalHasAllRequiredPermissions() {
    RBACAction action =
        new RBACAction(
            ACTION_ID, "ACT_EDIT", "Edit action", List.of(readPermission, writePermission));

    RBACAssignment assignment =
        new RBACAssignment(
            ASSIGNMENT_ID,
            principal,
            role,
            new RBACScope(null, List.of(resource)),
            new RBACContext(null, null, null, Set.of(structure)));

    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.findByPrincipalContextAndResource(
            eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
        .thenReturn(List.of(assignment));
    when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));

    boolean authorized = service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID);

    assertTrue(authorized);
  }

  @Test
  void isAuthorizedCachesActionPermissions() {
    RBACAction action =
        new RBACAction(ACTION_ID, "ACT_DISPLAY", "Display action", List.of(readPermission));

    RBACAssignment assignment =
        new RBACAssignment(
            ASSIGNMENT_ID,
            principal,
            role,
            new RBACScope(null, List.of(resource)),
            new RBACContext(null, null, null, Set.of(structure)));

    when(principalRepository.findByLogin(LOGIN)).thenReturn(Optional.of(principal));
    when(assignmentRepository.findByPrincipalContextAndResource(
            eq(LOGIN), any(RBACContext.class), eq(RESOURCE_ID)))
        .thenReturn(List.of(assignment));
    when(actionRepository.findById(ACTION_ID)).thenReturn(Optional.of(action));

    assertTrue(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID));
    assertTrue(service.isAuthorized(LOGIN, ACTION_ID, RESOURCE_ID));

    verify(actionRepository, times(1)).findById(ACTION_ID);
  }
}
