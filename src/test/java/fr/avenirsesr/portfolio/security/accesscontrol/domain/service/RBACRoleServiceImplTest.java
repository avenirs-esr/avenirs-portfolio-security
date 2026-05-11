package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

  @Test
  void getRoleByIdReturnsRoleWhenFound() {
    RBACRole role = role();

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));

    Optional<RBACRole> result = service.getRoleById(ROLE_ID);

    assertTrue(result.isPresent());
    assertEquals(role, result.orElseThrow());

    verify(roleRepository).findById(ROLE_ID);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getRoleByIdReturnsEmptyWhenNotFound() {
    when(roleRepository.findById(UNKNOWN_ROLE_ID)).thenReturn(Optional.empty());

    Optional<RBACRole> result = service.getRoleById(UNKNOWN_ROLE_ID);

    assertTrue(result.isEmpty());

    verify(roleRepository).findById(UNKNOWN_ROLE_ID);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getRoleByNameReturnsRoleWhenFound() {
    RBACRole role = role();

    when(roleRepository.findByName(ROLE_NAME)).thenReturn(Optional.of(role));

    Optional<RBACRole> result = service.getRoleByName(ROLE_NAME);

    assertTrue(result.isPresent());
    assertEquals(role, result.orElseThrow());

    verify(roleRepository).findByName(ROLE_NAME);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getRoleByNameReturnsEmptyWhenNotFound() {
    when(roleRepository.findByName(UNKNOWN_ROLE_NAME)).thenReturn(Optional.empty());

    Optional<RBACRole> result = service.getRoleByName(UNKNOWN_ROLE_NAME);

    assertTrue(result.isEmpty());

    verify(roleRepository).findByName(UNKNOWN_ROLE_NAME);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getAllRolesReturnsRepositoryRoles() {
    RBACRole role = role();

    when(roleRepository.findAll()).thenReturn(List.of(role));

    List<RBACRole> result = service.getAllRoles();

    assertThat(result).containsExactly(role);

    verify(roleRepository).findAll();
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getAllRolesReturnsEmptyListWhenRepositoryIsEmpty() {
    when(roleRepository.findAll()).thenReturn(List.of());

    List<RBACRole> result = service.getAllRoles();

    assertThat(result).isEmpty();

    verify(roleRepository).findAll();
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void createRoleSavesRole() {
    RBACRole roleToCreate = roleWithoutId();
    RBACRole savedRole = role();

    when(roleRepository.save(roleToCreate)).thenReturn(savedRole);

    RBACRole result = service.createRole(roleToCreate);

    assertEquals(savedRole, result);
    assertEquals(ROLE_ID, result.id());

    verify(roleRepository).save(roleToCreate);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void updateRoleSavesRoleWhenExistingRoleIsFound() {
    RBACRole role = role();

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
    when(roleRepository.save(role)).thenReturn(role);

    RBACRole result = service.updateRole(role);

    assertEquals(role, result);

    verify(roleRepository).findById(ROLE_ID);
    verify(roleRepository).save(role);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void updateRoleThrowsWhenRoleDoesNotExist() {
    RBACRole role = role();

    when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

    AccessControlNotFoundException exception =
        assertThrows(AccessControlNotFoundException.class, () -> service.updateRole(role));

    assertEquals(
        "Role not found, ID: 00000000-0000-0000-0000-000000000001", exception.getMessage());

    verify(roleRepository).findById(ROLE_ID);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void deleteRoleDeletesById() {
    service.deleteRole(ROLE_ID);

    verify(roleRepository).deleteById(ROLE_ID);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getRolesByPrincipalLoginReturnsDistinctRolesFromAssignments() {
    RBACRole owner = role();
    RBACRole pair =
        new RBACRole(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "ROLE_PAIR",
            "Can give feedback",
            Set.of(permission("PERM_READ"), permission("PERM_COMMENT")));

    when(assignmentRepository.findByPrincipal(LOGIN))
        .thenReturn(List.of(assignment(owner), assignment(owner), assignment(pair)));

    List<RBACRole> result = service.getRolesByPrincipalLogin(LOGIN);

    assertThat(result).containsExactly(owner, pair);

    verify(assignmentRepository).findByPrincipal(LOGIN);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
  }

  @Test
  void getRolesByPrincipalLoginReturnsEmptyListWhenNoAssignmentExists() {
    when(assignmentRepository.findByPrincipal(LOGIN)).thenReturn(List.of());

    List<RBACRole> result = service.getRolesByPrincipalLogin(LOGIN);

    assertThat(result).isEmpty();

    verify(assignmentRepository).findByPrincipal(LOGIN);
    verifyNoMoreInteractions(roleRepository, assignmentRepository);
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
    return new RBACAssignment(
        UUID.randomUUID(),
        new Principal(UUID.randomUUID(), LOGIN, "OIDC", LOGIN, UUID.randomUUID(), Set.of()),
        role,
        new RBACScope(UUID.randomUUID(), List.of()),
        new RBACContext(UUID.randomUUID(), null, null, Set.of()));
  }

  private RBACPermission permission(String name) {
    return new RBACPermission(UUID.randomUUID(), name, "");
  }
}
